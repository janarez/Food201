package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.food_sensing.dto.BoundingBoxDto;
import si.labs.augmented_reality_menu.food_sensing.factories.MaterialFactoryCache;
import si.labs.augmented_reality_menu.food_sensing.factories.RectangleFactory;
import si.labs.augmented_reality_menu.menu_display.MenuItemListAdapter;
import si.labs.augmented_reality_menu.menu_display.MenuValueHolder;
import si.labs.augmented_reality_menu.model.LabelValueNamePair;
import si.labs.augmented_reality_menu.model.ModelExecutor;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class BitmapProjector {
    private static final String TAG = BitmapProjector.class.getSimpleName();

    private final MaterialFactoryCache materialFactoryCache;
    private final RectangleFactory rectangleFactory;
    private final ArFragment arFragment;
    private final ARActivity arActivity;
    private final ModelExecutor modelExecutor;
    private final BitmapProcessing bitmapProcessing;
    private final DisplayMetrics displayMetrics;
    private final long deltaTime; // in millis
    private long nextProjectionTime; // in millis

    private ModelOutput modelOutput;
    private final List<AnchorNode> currentAnchors;

    public BitmapProjector(ArFragment arFragment, ARActivity arActivity, ModelExecutor modelExecutor) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
        this.modelExecutor = modelExecutor;
        this.bitmapProcessing = new BitmapProcessing();

        materialFactoryCache = new MaterialFactoryCache();
        rectangleFactory = new RectangleFactory(materialFactoryCache);
        nextProjectionTime = Calendar.getInstance().getTimeInMillis();
        deltaTime = 5000;
        currentAnchors = new LinkedList<>();

        displayMetrics = new DisplayMetrics();
        arActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public void onFrame() {

        long newTime = Calendar.getInstance().getTimeInMillis();

        // don't constantly try to classify
        if (nextProjectionTime > newTime) {
            return;
        } else {
            nextProjectionTime = deltaTime + newTime;
        }

        // if not tracking then wait
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Optional<MenuItemListAdapter> menuItemListAdapterOpt = arActivity.getMenuListAdapter();
        if (!menuItemListAdapterOpt.isPresent()) {
            return;
        }
        MenuItemListAdapter menuItemListAdapter = menuItemListAdapterOpt.get();
        List<MenuValueHolder> selectedValues = menuItemListAdapter.getSelectedValues();

        Optional<ModelOutput> modelOutputOpt = getModelOutput(frame);
        if (!modelOutputOpt.isPresent()) {
            return;
        }
        modelOutput = modelOutputOpt.get();
        updateMenuRenderable(modelOutput, menuItemListAdapter);

        projectPoints(frame, selectedValues);
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

    private void projectPoints(Frame frame, List<MenuValueHolder> selectedLabels) {

        Scene scene = arFragment.getArSceneView().getScene();

        // clean previous anchors
        for (AnchorNode currentAnchor : currentAnchors) {
            if (currentAnchor.getAnchor() != null) {
                currentAnchor.getAnchor().detach();
            }
            scene.removeChild(currentAnchor);
        }
        currentAnchors.clear();

        Set<Integer> labelsOfInterest = selectedLabels.stream()
                .map(MenuValueHolder::getLabelValue)
                .collect(Collectors.toSet());
        Bitmap mask = modelOutput.getMask();

        Collection<BoundingBoxDto> boundingBoxes = bitmapProcessing.getBoundingBoxes(mask, labelsOfInterest);

        for (BoundingBoxDto boxDto : boundingBoxes) {
            drawRectangle(frame, mask, boxDto);
        }
    }

    private Optional<HitResult> getHit(Frame frame, int maskX, int maskY, int maskWidth, int maskHeight) {
        float xRatio = displayMetrics.widthPixels * 1.0f / maskWidth;
        float yRatio = displayMetrics.heightPixels * 1.0f / maskHeight;

        List<HitResult> hits = frame.hitTest(Math.round(maskX * xRatio), maskY * yRatio);
        return getTheOptimalHit(hits);
    }

    private void updateMenuRenderable(ModelOutput modelOutput, MenuItemListAdapter menuItemListAdapter) {
        Set<MenuValueHolder> valueHolders = new HashSet<>();
        for (MenuValueHolder value : menuItemListAdapter.getValues()) {
            if (value.isSelected()) {
                valueHolders.add(value);
            }
        }
        for (LabelValueNamePair label : modelOutput.getLabels()) {
            valueHolders.add(new MenuValueHolder(label.getLabelName(), label.getLabelValue()));
        }

        List<MenuValueHolder> values = new ArrayList<>(valueHolders);
        values.sort(Comparator.comparing(MenuValueHolder::getLabel));

        menuItemListAdapter.getValues().clear();
        menuItemListAdapter.addAll(valueHolders);
        menuItemListAdapter.notifyDataSetChanged();
    }

    private void drawRectangle(Frame frame, Bitmap mask, BoundingBoxDto boxDto) {

        List<Optional<HitResult>> pointsOpt = new LinkedList<>();

        // min min
        Optional<HitResult> pointPosition1 = getHit(frame, boxDto.getXMin(), boxDto.getYMin(), mask.getWidth(), mask.getHeight());
        pointsOpt.add(pointPosition1);

        // min max
        Optional<HitResult> pointPosition2 = getHit(frame, boxDto.getXMin(), boxDto.getYMax(), mask.getWidth(), mask.getHeight());
        pointsOpt.add(pointPosition2);

        // max max
        Optional<HitResult> pointPosition4 = getHit(frame, boxDto.getXMax(), boxDto.getYMax(), mask.getWidth(), mask.getHeight());
        pointsOpt.add(pointPosition4);

        // max min
        Optional<HitResult> pointPosition3 = getHit(frame, boxDto.getXMax(), boxDto.getYMin(), mask.getWidth(), mask.getHeight());
        pointsOpt.add(pointPosition3);

        if (pointsOpt.stream().allMatch(Optional::isPresent)) {
            List<Node> points = pointsOpt.stream()
                    .map(Optional::get)
                    .map(hitResult -> {
                        AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
                        currentAnchors.add(anchorNode);
                        return anchorNode;
                    })
                    .collect(Collectors.toList());

            points.forEach(hitResult -> drawPoint(hitResult, boxDto.getClassOfInterest()));
            rectangleFactory.getSquare(arActivity, points, new Color(boxDto.getClassOfInterest()));
        }
    }

    private void drawPoint(Node anchorNode, int labelValue) {

        materialFactoryCache.makeOpaqueWithColor(arActivity, new Color(labelValue))
                .thenAccept(material -> {
                    ModelRenderable sphere = ShapeFactory.makeSphere(0.01f, Vector3.zero(), material);

                    anchorNode.setRenderable(sphere);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                });
    }
}
