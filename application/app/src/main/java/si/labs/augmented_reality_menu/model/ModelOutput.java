package si.labs.augmented_reality_menu.model;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.List;

/**
 * Encapsulates model output: overlayed mask + labels.
 */
public class ModelOutput {
    private final Bitmap mask;
    private final List<String> labels;

    public ModelOutput(Bitmap mask, List<String> labels) {
        this.mask = mask;
        this.labels = labels;
    }

    public String labelsAsSingleString() {
        return TextUtils.join(" - ", labels);
    }

    public Bitmap getMask() {
        return mask;
    }

    public List<String> getLabels() {
        return labels;
    }
}
