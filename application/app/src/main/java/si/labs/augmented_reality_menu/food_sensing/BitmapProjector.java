package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;

import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.List;

import si.labs.augmented_reality_menu.model.ModelOutput;

public class BitmapProjector {
    private final ArFragment arFragment;
    private Plane planeUnderPlate;

    public BitmapProjector(ArFragment arFragment) {
        this.arFragment = arFragment;
    }

    private void drawPoints(Scene scene, ModelOutput modelOutput) {
        List<String> labels = modelOutput.getLabels();
        Bitmap mask = modelOutput.getMask();
    }

    public void onFrame(FrameTime frameTime) {
        Session session = arFragment.getArSceneView().getSession();
        if (session == null) {
            return;
        }

        if (planeUnderPlate == null || planeUnderPlate.getTrackingState() != TrackingState.TRACKING) {
            Collection<Plane> planes = session.getAllTrackables(Plane.class);
            if (!planes.isEmpty()) {
                planeUnderPlate = planes.iterator().next();
            } else {
                return;
            }
        }
        if (planeUnderPlate.getSubsumedBy() != null) {
            planeUnderPlate = planeUnderPlate.getSubsumedBy();
        }
    }
}
