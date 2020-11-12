package si.labs.augmented_reality_menu.menu_display;

import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

public class DisplayOnPlaneTapImpl implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = DisplayOnPlaneTapImpl.class.getSimpleName();

    private final BaseArFragment arFragment;
    private final ViewRenderable menuRenderable;

    public DisplayOnPlaneTapImpl(BaseArFragment arFragment, ViewRenderable menuRenderable) {
        this.arFragment = arFragment;
        this.menuRenderable = menuRenderable;
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
    }
}
