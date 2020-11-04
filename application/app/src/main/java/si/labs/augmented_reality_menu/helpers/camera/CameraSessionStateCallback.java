package si.labs.augmented_reality_menu.helpers.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.util.Log;

import androidx.annotation.NonNull;

public class CameraSessionStateCallback extends CameraCaptureSession.StateCallback {
    private final Runnable onConfigured;
    private final Runnable onActive;
    private CameraCaptureSession cameraCaptureSession;

    public CameraSessionStateCallback(Runnable onConfigured, Runnable onActive) {
        this.onConfigured = onConfigured;
        this.onActive = onActive;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        Log.d(CameraCaptureSession.class.getName(), "Camera capture session configured");
        cameraCaptureSession = session;
        onConfigured.run();
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        Log.e(CameraCaptureSession.class.getName(), "Failed to configure camera capture session.");
    }

    @Override
    public void onActive(@NonNull CameraCaptureSession session) {
        super.onActive(session);
        onActive.run();
    }

    public CameraCaptureSession getCameraCaptureSession() {
        return cameraCaptureSession;
    }
}
