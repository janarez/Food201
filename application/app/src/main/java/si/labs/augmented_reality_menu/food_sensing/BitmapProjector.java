package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.List;

import si.labs.augmented_reality_menu.model.ModelOutput;

public class BitmapProjector {
    private final ArFragment arFragment;

    public BitmapProjector(ArFragment arFragment) {
        this.arFragment = arFragment;
    }

    private void drawPoints(Scene scene, ModelOutput modelOutput) {
        List<String> labels = modelOutput.getLabels();
        Bitmap mask = modelOutput.getMask();


    }
}
