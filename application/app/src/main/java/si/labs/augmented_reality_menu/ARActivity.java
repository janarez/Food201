package si.labs.augmented_reality_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import si.labs.augmented_reality_menu.helpers.ar.ARCheckerHelper;
import si.labs.augmented_reality_menu.helpers.FullScreenHelper;
import si.labs.augmented_reality_menu.helpers.CameraPermissionHelper;
import si.labs.augmented_reality_menu.helpers.opengl.BackgroundRenderer;

public class ARActivity extends AppCompatActivity {

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private ARCheckerHelper arCheckerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_r);

        surfaceView = findViewById(R.id.surfaceview);
        arCheckerHelper = new ARCheckerHelper(this);

        BackgroundRenderer renderer = new BackgroundRenderer();
        renderer.configureSurfaceView(surfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        arCheckerHelper.onActivityResume();
        surfaceView.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();

        surfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        arCheckerHelper.onActivityDestroy();
        super.onDestroy();
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