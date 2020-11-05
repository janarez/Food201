package si.labs.augmented_reality_menu.helpers.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.google.ar.core.SharedCamera;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.util.List;

import si.labs.augmented_reality_menu.R;
import si.labs.augmented_reality_menu.helpers.ARCheckerHelper;
import si.labs.augmented_reality_menu.helpers.CameraPermissionHelper;

public class CameraHelper {
    private static final String cameraBackgroundThreadName = "sharedCameraBackground";

    private final Activity boundActivity;
    private final ARCheckerHelper arCheckerHelper;

    private SharedCamera sharedCamera;
    private String sharedCameraId;
    private CameraDeviceStateCallback cameraDeviceStateCallback;
    private CameraSessionStateCallback cameraSessionStateCallback;
    private CameraCaptureSessionCaptureCallback cameraCaptureSessionCaptureCallback;
    private CameraManager cameraManager;
    private CaptureRequest captureRequest;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    public CameraHelper(Activity activity, ARCheckerHelper arCheckerHelper) {
        this.boundActivity = activity;
        this.arCheckerHelper = arCheckerHelper;
    }

    public void onActivityResume() {
        this.sharedCamera = arCheckerHelper.getSession().getSharedCamera();
        this.sharedCameraId = arCheckerHelper.getSession().getCameraConfig().getCameraId();
        this.cameraDeviceStateCallback = new CameraDeviceStateCallback(this::createCameraPreviewSession);
        cameraSessionStateCallback = new CameraSessionStateCallback(this::setRepeatingCaptureRequest, this::resumeARCore);
        cameraCaptureSessionCaptureCallback = new CameraCaptureSessionCaptureCallback();

        // Store a reference to the camera system service.
        this.cameraManager = (CameraManager) boundActivity.getSystemService(Context.CAMERA_SERVICE);
        startBackgroundThread();
        openCamera();
    }

    public void onActivityPause() {
        stopBackgroundThread();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread(cameraBackgroundThreadName);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread == null) {
            return;
        }

        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            Log.e(CameraHelper.class.getName(), "Interrupted while trying to join background handler thread", e);
        }
    }

    private void openCamera() {
        // Wrap our callback in a shared camera callback.
        CameraDevice.StateCallback wrappedCallback =
                sharedCamera.createARDeviceStateCallback(cameraDeviceStateCallback, backgroundHandler);

        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(boundActivity)) {
            CameraPermissionHelper.requestCameraPermission(boundActivity);
        }
        try {
            cameraManager.openCamera(sharedCameraId, wrappedCallback, backgroundHandler);
        } catch (SecurityException e) {
            Log.e(CameraHelper.class.getName(), "Camera permission was not granted", e);
            Toast.makeText(boundActivity, R.string.camera_permission_not_granted, Toast.LENGTH_LONG)
                    .show();
        } catch (CameraAccessException e) {
            Log.e(CameraHelper.class.getName(), "Exception while trying to access the camera", e);
            Toast.makeText(boundActivity, R.string.camera_can_not_access, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void createCameraPreviewSession() {
        try {
            CaptureRequest.Builder builder = cameraDeviceStateCallback.getCameraDevice().createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaceList = sharedCamera.getArCoreSurfaces();

            for (Surface surface : surfaceList) {
                builder.addTarget(surface);
            }

            captureRequest = builder.build();
            CameraCaptureSession.StateCallback wrappedCallback = sharedCamera.createARSessionStateCallback(cameraSessionStateCallback, backgroundHandler);
            cameraDeviceStateCallback.getCameraDevice().createCaptureSession(surfaceList, wrappedCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(CameraHelper.class.getName(), "Exception while trying to access the camera", e);
            Toast.makeText(boundActivity, R.string.camera_can_not_access, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void setRepeatingCaptureRequest() {
        try {
            cameraSessionStateCallback.getCameraCaptureSession()
                    .setRepeatingRequest(captureRequest, cameraCaptureSessionCaptureCallback,backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(CameraHelper.class.getName(), "Exception while trying to access the camera", e);
            Toast.makeText(boundActivity, R.string.camera_can_not_access, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void resumeARCore() {
        try {
            arCheckerHelper.getSession().resume();
            sharedCamera.setCaptureCallback(cameraCaptureSessionCaptureCallback, backgroundHandler);
        } catch (CameraNotAvailableException e) {
            Log.e(CameraHelper.class.getName(), "Camera not available", e);
            Toast.makeText(boundActivity, R.string.camera_not_available, Toast.LENGTH_LONG)
                    .show();
        }
    }
}
