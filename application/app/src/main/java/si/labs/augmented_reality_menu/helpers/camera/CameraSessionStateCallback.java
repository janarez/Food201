package si.labs.augmented_reality_menu.helpers.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.util.Log;

import androidx.annotation.NonNull;

public class CameraSessionStateCallback extends CameraCaptureSession.StateCallback {
    private CameraCaptureSession cameraCaptureSession;

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        Log.d(CameraCaptureSession.class.getName(), "Camera capture session configured");
        cameraCaptureSession = session;
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.e(CameraCaptureSession.class.getName(), "Failed to configure camera capture session.");
    }

    public CameraCaptureSession getCameraCaptureSession() {
        return cameraCaptureSession;
    }
}
