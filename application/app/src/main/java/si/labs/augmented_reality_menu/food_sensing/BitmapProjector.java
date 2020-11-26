package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.List;
import java.util.Optional;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class BitmapProjector {
    private static final int maxDepth = 1; // 1 meter
    private final ArFragment arFragment;
    private final ARActivity arActivity;

    public BitmapProjector(ArFragment arFragment, ARActivity arActivity) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
    }

    private void drawPoint(Scene scene, HitResult hitResult, int maskValue) {
        Color sphereColor = new Color(); // TODO
        sphereColor.set(maskValue);
        MaterialFactory.makeOpaqueWithColor(arActivity, sphereColor)
                .thenAccept(material -> {
                    ModelRenderable sphere = ShapeFactory.makeSphere(0.1f, new Vector3(0, 0, 0), material);

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setRenderable(sphere);
                    anchorNode.setParent(scene);
                });
    }

    public void onFrame(ModelOutput modelOutput) {
        Scene scene = arFragment.getArSceneView().getScene();
        List<String> labels = modelOutput.getLabels();
        Bitmap mask = modelOutput.getMask();

        for (int i = 0; i < mask.getHeight(); i++) {
            for (int j = 0; j < mask.getWidth(); j++) {
                List<HitResult> hits = arFragment.getArSceneView().getArFrame().hitTest(i, j);
                Optional<HitResult> result = hits.stream().filter(hitResult -> hitResult.getDistance() < maxDepth).findAny();
                int pixelValue = mask.getPixel(i, j);

                result.ifPresent(hitResult -> drawPoint(scene, hitResult, pixelValue));
            }
        }
    }
}
