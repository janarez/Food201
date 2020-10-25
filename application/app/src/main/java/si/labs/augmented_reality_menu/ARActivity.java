package si.labs.augmented_reality_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import si.labs.augmented_reality_menu.helpers.ARCheckerHelper;
import si.labs.augmented_reality_menu.helpers.camera.CameraHelper;
import si.labs.augmented_reality_menu.helpers.CameraPermissionHelper;

public class ARActivity extends AppCompatActivity {

    private ARCheckerHelper arCheckerHelper;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_r);

        arCheckerHelper = new ARCheckerHelper(this);
        cameraHelper = new CameraHelper(this, arCheckerHelper);
    }

    @Override
    protected void onResume() {
        super.onResume();

        arCheckerHelper.requestInstall();
        cameraHelper.onActivityResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

}