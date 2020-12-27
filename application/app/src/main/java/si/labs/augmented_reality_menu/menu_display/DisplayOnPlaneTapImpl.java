package si.labs.augmented_reality_menu.menu_display;

import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.food_sensing.BitmapProjector;
import si.labs.augmented_reality_menu.model.LabelValueNamePair;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class DisplayOnPlaneTapImpl implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = DisplayOnPlaneTapImpl.class.getSimpleName();

    private final BaseArFragment arFragment;
    private final ARActivity arActivity;
    private final BitmapProjector bitmapProjector;
    private final MenuItemListAdapter menuItemListAdapter;
    private TransformableNode menuNode;

    public DisplayOnPlaneTapImpl(BaseArFragment arFragment, ARActivity arActivity,
                                 BitmapProjector bitmapProjector, MenuItemListAdapter menuItemListAdapter) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
        this.bitmapProjector = bitmapProjector;
        this.menuItemListAdapter = menuItemListAdapter;
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        Optional<ModelOutput> modelOutputOpt = bitmapProjector.getModelOutput();
        if (!modelOutputOpt.isPresent()) {
            return;
        }
        ModelOutput modelOutput = modelOutputOpt.get();

        Optional<ViewRenderable> menu = arActivity.getMenuRenderable();
        if (!menu.isPresent()) {
            return;
        }

        if (menuNode == null) {
            menuNode = new TransformableNode(arFragment.getTransformationSystem());
            menuNode.setRenderable(menu.get());

            Quaternion menuRotationY = Quaternion.axisAngle(new Vector3(0, 1, 0), 180f);
            Quaternion menuRotationX = Quaternion.axisAngle(new Vector3(1, 0, 0), -90);

            menuNode.setLocalRotation(Quaternion.multiply(menuRotationX, menuRotationY));
        }

        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        menuNode.setParent(anchorNode);
        menuNode.setRenderable(menu.get());

        updateMenuRenderable(modelOutput);
    }

    private void updateMenuRenderable(ModelOutput modelOutput) {
        List<MenuValueHolder> valueHolders = new LinkedList<>();
        for (LabelValueNamePair label : modelOutput.getLabels()) {
            valueHolders.add(new MenuValueHolder(label.getLabelName(), label.getLabelValue()));
        }

        menuItemListAdapter.getValues().clear();
        menuItemListAdapter.addAll(valueHolders);
        menuItemListAdapter.notifyDataSetChanged();
    }
}
