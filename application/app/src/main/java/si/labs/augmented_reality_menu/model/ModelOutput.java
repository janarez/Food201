package si.labs.augmented_reality_menu.model;

import android.graphics.Bitmap;

import java.util.HashSet;

/**
 * Encapsulates model output: overlayed mask + labels.
 */
public class ModelOutput {
    public final Bitmap mask;
    public final HashSet<Integer> labels;

    public ModelOutput(Bitmap mask, HashSet<Integer> labels) {
        this.mask = mask;
        this.labels = labels;
    }
}
