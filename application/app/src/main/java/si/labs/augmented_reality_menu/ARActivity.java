package si.labs.augmented_reality_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import si.labs.augmented_reality_menu.menu_display.DisplayOnPlaneTapImpl;

public class ARActivity extends AppCompatActivity {
    private static final double MIN_OPENGL_VERSION = 3.0;
    private static final String TAG = ARActivity.class.getSimpleName();

    private ArFragment arFragment;
    private ViewRenderable menuRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        setContentView(R.layout.activity_a_r);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arFragment.setOnTapArPlaneListener(new DisplayOnPlaneTapImpl(arFragment, this));

        CompletableFuture<ViewRenderable> menuRenderableFuture =
                ViewRenderable.builder().setView(this, R.layout.menu_layout_a_r).build();

        menuRenderableFuture.handle((viewRenderable, throwable) -> {
            if (throwable != null) {
                Toast.makeText(this, R.string.error_creating_menu_view, Toast.LENGTH_LONG)
                        .show();
                Log.e(TAG, getResources().getString(R.string.error_creating_menu_view), throwable);
            }

            menuRenderable = viewRenderable;
            return null;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arFragment.onDestroy();
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

    public Optional<ViewRenderable> getMenuRenderable() {
        return Optional.ofNullable(menuRenderable);
    }
}