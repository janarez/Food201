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

import java.util.Optional;

import si.labs.augmented_reality_menu.ARActivity;

public class DisplayOnPlaneTapImpl implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = DisplayOnPlaneTapImpl.class.getSimpleName();

    private final BaseArFragment arFragment;
    private final ARActivity arActivity;
    private TransformableNode menuNode;

    public DisplayOnPlaneTapImpl(BaseArFragment arFragment, ARActivity arActivity) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
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
    }
}
