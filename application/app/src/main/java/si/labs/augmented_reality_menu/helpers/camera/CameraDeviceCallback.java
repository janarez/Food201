package si.labs.augmented_reality_menu.helpers.camera;

import android.hardware.camera2.CameraDevice;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

public class CameraDeviceCallback extends CameraDevice.StateCallback {
    private CameraDevice cameraDevice;
    private final Runnable onCameraOpen;

    public CameraDeviceCallback(Runnable onCameraOpen) {
        this.onCameraOpen = onCameraOpen;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        String logMessage =  String.format("Camera with id %s opened", camera.getId());
        Log.d(CameraDeviceCallback.class.getName(), logMessage);
        this.cameraDevice = camera;
        onCameraOpen.run();
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        String logMessage =  String.format("Camera with id %s disconnected", camera.getId());
        Log.w(CameraDeviceCallback.class.getName(), logMessage);
        camera.close();
        this.cameraDevice = null;
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        String logMessage =  String.format(Locale.ROOT, "Camera with id %s error %d", camera.getId(), error);
        Log.e(CameraDeviceCallback.class.getName(), logMessage);
        camera.close();
        this.cameraDevice = null;
    }

    public CameraDevice getCameraDevice() {
        return cameraDevice;
    }
}
