package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
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
import java.util.Random;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.food_sensing.dto.FrameHitDataDto;
import si.labs.augmented_reality_menu.model.ModelExecutor;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class BitmapProjector {
    private static final String TAG = BitmapProjector.class.getSimpleName();
    private static final int MAX_DEPTH = 1; // 1 meter
    private static final float MIN_DEPTH = 0.1f;

    private final ArFragment arFragment;
    private final ARActivity arActivity;
    private final ModelExecutor modelExecutor;
    private final BitmapProcessing bitmapProcessing;
    private final Random randomGenerator;
    private ModelRenderable sphere;

    private final DisplayMetrics displayMetrics;
    private final long deltaTime; // in millis
    private long nextProjectionTime; // in millis

    public BitmapProjector(ArFragment arFragment, ARActivity arActivity, ModelExecutor modelExecutor) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
        this.modelExecutor = modelExecutor;
        this.bitmapProcessing = new BitmapProcessing();

        randomGenerator = new Random();
        nextProjectionTime = Calendar.getInstance().getTimeInMillis();
        deltaTime = 5000;

        displayMetrics = new DisplayMetrics();
        arActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        MaterialFactory.makeOpaqueWithColor(arActivity, new Color(0, 0, 0)).thenAccept(material -> {
            sphere = ShapeFactory.makeSphere(0.01f, new Vector3(0, 0, 0), material);
        });
    }

    private void drawPoint(HitResult relativePosition, AnchorNode anchorNode) {

        Pose hitPose = relativePosition.getHitPose();

        Node childNode = new Node();
        childNode.setParent(anchorNode);
        childNode.setWorldPosition(new Vector3(hitPose.tx(), hitPose.ty(), hitPose.tz()));
        childNode.setRenderable(sphere);
    }

    public void onFrame() {

        // if not tracking then wait
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        if (sphere == null) {
            return;
        }

        long newTime = Calendar.getInstance().getTimeInMillis();

        // don't constantly try to classify
        if (nextProjectionTime > newTime) {
            return;
        } else {
            nextProjectionTime = deltaTime + newTime;
        }

        Optional<FrameHitDataDto> frameHitDataDtoOptional = hitRandomPoint(frame);
        if (!frameHitDataDtoOptional.isPresent()) {
            return;
        }
        FrameHitDataDto frameHitDataDto = frameHitDataDtoOptional.get();

        Scene scene = arFragment.getArSceneView().getScene();
        Anchor mainAnchor = frameHitDataDto.getCentralPointHit().createAnchor();
        AnchorNode anchorNode = new AnchorNode(mainAnchor);
        anchorNode.setParent(scene);
        anchorNode.setRenderable(sphere);

        projectPoints(frame, frameHitDataDto, anchorNode);

//        sphereDataFuture.handle((sphereDataDtos,throwable) -> {
//            for (SphereDataDto sphereDataDto : sphereDataDtos) {
//                drawPoint(sphereDataDto.getLocalPosition(), anchorNode);
//            }
//            return null;
//        });
    }

    private Optional<FrameHitDataDto> hitRandomPoint(Frame frame) {
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int randomX = randomGenerator.nextInt(screenWidth - 1);
        int randomY = randomGenerator.nextInt(screenHeight - 1);

        List<HitResult> centerHits = frame.hitTest(randomY, randomX);

        Optional<HitResult> optimalHitOpt = getTheOptimalHit(centerHits);

        if (optimalHitOpt.isPresent()) {
            HitResult optimalHit = optimalHitOpt.get();

            FrameHitDataDto out = new FrameHitDataDto(randomX, randomY, optimalHit);

            return Optional.of(out);
        } else {
            return Optional.empty();
        }
    }

    private Optional<HitResult> getTheOptimalHit(List<HitResult> hits) {
        return hits.stream()
//                .filter(hitResult -> hitResult.getDistance() < maxDepth)
//                .filter(hitResult -> hitResult.getDistance() > minDepth)
                .filter(hitResult -> {
                    Trackable trackable = hitResult.getTrackable();
                    return trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hitResult.getHitPose());
                })
                .min(Comparator.comparingDouble(HitResult::getDistance));
    }

    private Optional<ModelOutput> getModelOutput(Frame frame) {
        try (Image sceneImage = frame.acquireCameraImage()) {
            Log.d(TAG, String.format("Acquired scene image [%d, %d] in format %d",
                    sceneImage.getHeight(), sceneImage.getWidth(), sceneImage.getFormat()));

            // The scene image should and must be in given format as per documentation.
            if (sceneImage.getFormat() != ImageFormat.YUV_420_888) {
                throw new UnsupportedOperationException(
                        String.format("Cannot process scene image in format %d (only 35 is accepted)", sceneImage.getFormat()));
            }

            // Pass to model.
            return Optional.of(modelExecutor.run(sceneImage));
        } catch (NotYetAvailableException e) {
            Log.e(TAG, "Could not get scene image.");
            return Optional.empty();
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, e.getMessage());
            return Optional.empty();
        }
    }

    private void projectPoints(Frame frame, FrameHitDataDto frameHitDataDto, AnchorNode anchor) {
        Optional<ModelOutput> modelOutputOpt = getModelOutput(frame);
        if (!modelOutputOpt.isPresent()) {
            return;
        }
        ModelOutput modelOutput = modelOutputOpt.get();
        List<String> labels = modelOutput.getLabels();

//        StringBuilder builder = new StringBuilder();
//        for (String label : labels) {
//            builder.append(label).append(" ");
//        }
//        Toast.makeText(arActivity, builder.toString(), Toast.LENGTH_LONG).show();

        Bitmap mask = modelOutput.getMask();

        Bitmap edges = bitmapProcessing.getEdges(mask);

        for (int i = 0; i < mask.getHeight(); i++) {
            for (int j = 0; j < mask.getWidth(); j++) {
                if (edges.getPixel(j, i) != 0) {
                    Optional<HitResult> pointPosition = getHit(frame, j, i, mask.getWidth(), mask.getHeight());
                    pointPosition.ifPresent(hitResult -> drawPoint(hitResult, anchor));
                }
            }
        }
    }

    private Optional<HitResult> getHit(Frame frame, int maskX, int maskY, int maskWidth, int maskHeight) {
        float xRatio = displayMetrics.widthPixels * 1.0f / maskWidth;
        float yRatio = displayMetrics.heightPixels * 1.0f / maskHeight;

        List<HitResult> hits = frame.hitTest(Math.round(maskX * xRatio), maskY * yRatio);
        return getTheOptimalHit(hits);
    }
}
