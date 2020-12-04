package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.model.ModelExecutor;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class BitmapProjector {
    private static final String TAG = BitmapProjector.class.getSimpleName();
    private static final int maxDepth = 1; // 1 meter
    private static final float minDepth = 0.1f;
    private final ArFragment arFragment;
    private final ARActivity arActivity;
    private final ModelExecutor modelExecutor;

    private DisplayMetrics displayMetrics;
    private final long deltaTime; // in millis
    private long nextProjectionTime; // in millis

    public BitmapProjector(ArFragment arFragment, ARActivity arActivity, ModelExecutor modelExecutor) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
        this.modelExecutor = modelExecutor;

        nextProjectionTime = Calendar.getInstance().getTimeInMillis();
        deltaTime = 5000;

        displayMetrics = new DisplayMetrics();
        arActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void drawPoint(Scene scene, HitResult hitResult, int maskValue) {
        Color sphereColor = new Color(); // TODO
        sphereColor.set(maskValue);
        MaterialFactory.makeOpaqueWithColor(arActivity, sphereColor)
                .thenAccept(material -> {
                    ModelRenderable sphere = ShapeFactory.makeSphere(0.01f, new Vector3(0, 0, 0), material);

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setRenderable(sphere);
                    anchorNode.setParent(scene);
                });
    }

    public void onFrame() {

        long newTime = Calendar.getInstance().getTimeInMillis();

        if (nextProjectionTime > newTime) {
            return;
        } else {
            nextProjectionTime = deltaTime + newTime;
        }

        ModelOutput modelOutput;
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null) {
            return;
        }

        try (Image sceneImage = frame.acquireCameraImage()) {
            Log.d(TAG, String.format("Acquired scene image [%d, %d] in format %d",
                    sceneImage.getHeight(), sceneImage.getWidth(), sceneImage.getFormat()));

            // The scene image should and must be in given format as per documentation.
            if (sceneImage.getFormat() != ImageFormat.YUV_420_888) {
                throw new UnsupportedOperationException(
                        String.format("Cannot process scene image in format %d (only 35 is accepted)", sceneImage.getFormat()));
            }

            // Pass to model.
            modelOutput = modelExecutor.run(sceneImage);
        } catch (NotYetAvailableException e) {
            Log.e(TAG, "Could not get scene image.");
            return;
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        Scene scene = arFragment.getArSceneView().getScene();
        List<String> labels = modelOutput.getLabels();
        Bitmap mask = modelOutput.getMask();

        float xRatio = displayMetrics.widthPixels * 1.0f / mask.getWidth();
        float yRatio = displayMetrics.heightPixels * 1.0f / mask.getHeight();

        for (int i = 0; i < mask.getWidth(); i += 100) {
            for (int j = 0; j < mask.getHeight(); j += 100) {
                List<HitResult> hits = frame.hitTest(Math.round(i * xRatio), j * yRatio);
                Optional<HitResult> result = hits.stream()
                        .filter(hitResult -> hitResult.getDistance() < maxDepth)
                        .filter(hitResult -> hitResult.getDistance() > minDepth)
                        .min(Comparator.comparingDouble(HitResult::getDistance));
                int pixelValue = mask.getPixel(i, j);
                result.ifPresent(hitResult -> drawPoint(scene, hitResult, pixelValue));
            }
        }
    }
}
