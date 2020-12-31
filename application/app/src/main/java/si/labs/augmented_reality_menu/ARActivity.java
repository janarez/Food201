package si.labs.augmented_reality_menu;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.ux.ArFragment;

import java.util.LinkedList;
import java.util.Optional;

import si.labs.augmented_reality_menu.food_sensing.BitmapProjector;
import si.labs.augmented_reality_menu.menu_display.LabelMenuDialog;
import si.labs.augmented_reality_menu.menu_display.MainMenuDialog;
import si.labs.augmented_reality_menu.menu_display.MenuItemListAdapter;
import si.labs.augmented_reality_menu.model.ModelExecutor;

public class ARActivity extends AppCompatActivity {
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final String TAG = ARActivity.class.getSimpleName();

    private ArFragment arFragment;
    private MenuItemListAdapter menuItemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        // Loads and then runs model on AR camera images.
        ModelExecutor modelExecutor = new ModelExecutor(getApplicationContext());

        setContentView(R.layout.activity_a_r);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        menuItemListAdapter = new MenuItemListAdapter(this, 0, new LinkedList<>());

        Button labelMenuButton = findViewById(R.id.label_menu_button);
        LabelMenuDialog labelMenuDialog = new LabelMenuDialog(this, menuItemListAdapter);
        labelMenuButton.setOnClickListener(v -> labelMenuDialog.show());

        Button mainMenuButton = findViewById(R.id.ar_open_main_menu_button);
        MainMenuDialog mainMenuDialog = new MainMenuDialog(this);
        mainMenuButton.setOnClickListener(v -> mainMenuDialog.show());

        BitmapProjector bitmapProjector = new BitmapProjector(arFragment, this, modelExecutor);

        Button resenseButton = findViewById(R.id.menu_resense_button);
        resenseButton.setOnClickListener(v -> bitmapProjector.detect());

        // required so that spinners do not break full screen
        // TODO doesn't help
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (arFragment != null) {
            arFragment.onDestroy();
        }
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    public Optional<MenuItemListAdapter> getMenuListAdapter() {
        return Optional.ofNullable(menuItemListAdapter);
    }
}