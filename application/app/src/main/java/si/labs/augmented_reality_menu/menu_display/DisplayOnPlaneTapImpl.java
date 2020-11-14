package si.labs.augmented_reality_menu.menu_display;

import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;

import java.util.Optional;

import si.labs.augmented_reality_menu.ARActivity;

public class DisplayOnPlaneTapImpl implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = DisplayOnPlaneTapImpl.class.getSimpleName();

    private final BaseArFragment arFragment;
    private final ARActivity arActivity;
    private Node menuNode;

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
            menuNode = new Node();
            menuNode.setRenderable(menu.get());
        }

        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        menuNode.setParent(anchorNode);
    }
}
