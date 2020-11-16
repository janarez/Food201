package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.Image;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Optional;

public class ImageClassification {
    private final static String TAG = ImageClassification.class.getSimpleName();
    private final ArFragment arFragment;

    public ImageClassification(ArFragment arFragment) {
        this.arFragment = arFragment;
    }

    private Optional<Image> getImage() {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if (frame == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(frame.acquireCameraImage());
        } catch (NotYetAvailableException e) {
            Log.e(TAG, "AR core is not available yet", e);
        }
        return Optional.empty();
    }

    private Optional<Bitmap> getImageBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();

        switch (image.getFormat()) {
            case ImageFormat.JPEG:
                /* fallthrough */
            case ImageFormat.HEIC:
                byte[] imageBytes = planes[0].getBuffer().array();
                return Optional.of(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            default:
                Log.e(TAG, "Unknown format");
                return Optional.empty();
        }
    }
}
