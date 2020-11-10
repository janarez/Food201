package si.labs.augmented_reality_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import si.labs.augmented_reality_menu.helpers.ar.ARSessionHelper;
import si.labs.augmented_reality_menu.helpers.FullScreenHelper;
import si.labs.augmented_reality_menu.helpers.CameraPermissionHelper;
import si.labs.augmented_reality_menu.helpers.opengl.DisplayRotationHelper;
import si.labs.augmented_reality_menu.helpers.opengl.GeneralRenderer;

public class ARActivity extends AppCompatActivity {

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private ARSessionHelper arSessionHelper;
    private DisplayRotationHelper displayRotationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_r);

        surfaceView = findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(this);
        arSessionHelper = new ARSessionHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestCameraPermission();
        arSessionHelper.onActivityResume();
        new GeneralRenderer(surfaceView, arSessionHelper.getSession(), getAssets(), displayRotationHelper);
        surfaceView.onResume();
        displayRotationHelper.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (arSessionHelper.getSession() != null) {
            displayRotationHelper.onPause();
            surfaceView.onPause();
            arSessionHelper.onActivityPause();
        }
    }

    @Override
    protected void onDestroy() {
        arSessionHelper.onActivityDestroy();
        super.onDestroy();
    }

    private void requestCameraPermission() {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is required to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

}