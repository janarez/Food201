package si.labs.augmented_reality_menu.food_sensing;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.util.function.Consumer;

import si.labs.augmented_reality_menu.model.ModelExecutor;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class MaskProjector implements ImageAnalysis.Analyzer {

    private final ModelExecutor modelExecutor;
    private final Consumer<ModelOutput> callback;

    public MaskProjector(Context context, Consumer<ModelOutput> callback) {
        this.callback = callback;
        modelExecutor = new ModelExecutor(context);
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (image.getImage() != null) {
            ModelOutput output = modelExecutor.run(image.getImage());
            callback.accept(output);
        }
        image.close();
    }
}
