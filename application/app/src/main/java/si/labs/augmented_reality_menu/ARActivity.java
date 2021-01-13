package si.labs.augmented_reality_menu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.LinkedList;

import si.labs.augmented_reality_menu.menu_display.LabelMenuDialog;
import si.labs.augmented_reality_menu.menu_display.MainMenuDialog;
import si.labs.augmented_reality_menu.menu_display.MenuItemListAdapter;
import si.labs.augmented_reality_menu.model.ModelExecutor;

public class ARActivity extends AppCompatActivity {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PERM = 1000;

    private MenuItemListAdapter menuItemListAdapter;
    private String[] requiredPermissions;

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