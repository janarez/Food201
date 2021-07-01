package si.labs.augmented_reality_menu.model;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.List;

/**
 * Encapsulates model output: overlayed mask + labels.
 */
public class ModelOutput {
    private final Bitmap mask;
    private final List<LabelValueNamePair> labels;

    public ModelOutput(Bitmap mask, List<LabelValueNamePair> labels) {
        this.mask = mask;
        this.labels = labels;
    }

    public String labelsAsSingleString() {
        return TextUtils.join(" - ", labels);
    }

    public Bitmap getMask() {
        return mask;
    }

    public List<LabelValueNamePair> getLabels() {
        return labels;
    }
}
