package si.labs.augmented_reality_menu.helpers.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.ar.core.SharedCamera;

import si.labs.augmented_reality_menu.helpers.ARCheckerHelper;
import si.labs.augmented_reality_menu.helpers.CameraPermissionHelper;

public class CameraHelper {
    private final String cameraBackgroundThreadName = "sharedCameraBackground";

    private final Activity boundActivity;

    private final SharedCamera sharedCamera;
    private final String sharedCameraId;
    private final CameraDeviceCallback cameraDeviceCallback;
    private final CameraManager cameraManager;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    public CameraHelper(Activity activity, ARCheckerHelper arCheckerHelper) {
        this.boundActivity = activity;

        this.sharedCamera = arCheckerHelper.getSession().getSharedCamera();
        this.sharedCameraId = arCheckerHelper.getSession().getCameraConfig().getCameraId();
        this.cameraDeviceCallback = new CameraDeviceCallback();
        // Store a reference to the camera system service.
        this.cameraManager = (CameraManager) boundActivity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void onActivityResume() {
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

    public void openCamera() {
        // Wrap our callback in a shared camera callback.
        CameraDevice.StateCallback wrappedCallback =
                sharedCamera.createARDeviceStateCallback(cameraDeviceCallback, backgroundHandler);

        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(boundActivity)) {
            CameraPermissionHelper.requestCameraPermission(boundActivity);
        }

        // Open the camera device using the ARCore wrapped callback.
        cameraManager.openCamera(sharedCameraId, wrappedCallback, backgroundHandler);
    }
}
