package si.labs.augmented_reality_menu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import si.labs.augmented_reality_menu.menu_display.LabelMenuDialog;
import si.labs.augmented_reality_menu.menu_display.MainMenuDialog;
import si.labs.augmented_reality_menu.menu_display.MenuItemListAdapter;
import si.labs.augmented_reality_menu.model.ModelExecutor;

public class ARActivity extends AppCompatActivity {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PERM = 1000;

    private MenuItemListAdapter menuItemListAdapter;
    private String[] requiredPermissions;
    private PreviewView previewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requiredPermissions = new String[]{Manifest.permission.CAMERA};

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERM);
        }

        // Loads and then runs model on AR camera images.
        ModelExecutor modelExecutor = new ModelExecutor(getApplicationContext());

        setContentView(R.layout.activity_a_r);
        menuItemListAdapter = new MenuItemListAdapter(this, 0, new LinkedList<>());

        Button labelMenuButton = findViewById(R.id.label_menu_button);
        LabelMenuDialog labelMenuDialog = new LabelMenuDialog(this, menuItemListAdapter);
        labelMenuButton.setOnClickListener(v -> labelMenuDialog.show());

        Button mainMenuButton = findViewById(R.id.ar_open_main_menu_button);
        MainMenuDialog mainMenuDialog = new MainMenuDialog(this, menuItemListAdapter);
        mainMenuButton.setOnClickListener(v -> mainMenuDialog.show());

        Button resenseButton = findViewById(R.id.menu_resense_button);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFut = ProcessCameraProvider.getInstance(this);
        cameraProviderFut.addListener(() -> {
            Preview preview = new Preview.Builder().build();
            previewView = findViewById(R.id.preview_view);
            preview.setSurfaceProvider(previewView.getSurfaceProvider());
            CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFut.get();
                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(this, selector, preview);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {

        for (String requiredPermission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERM) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, R.string.error_perm, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}