package si.labs.augmented_reality_menu.menu_display;

import android.graphics.ImageFormat;
import android.media.Image;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.ux.BaseArFragment;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.model.ModelExecutor;

public class DisplayOnPlaneTapImpl implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = DisplayOnPlaneTapImpl.class.getSimpleName();

    private final BaseArFragment arFragment;
    private final ARActivity arActivity;
    private final ModelExecutor modelExecutor;

    public DisplayOnPlaneTapImpl(ModelExecutor modelExecutor, BaseArFragment arFragment, ARActivity arActivity) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
        this.modelExecutor = modelExecutor;
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        // Get camera scene for the model.
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            // Copy the camera stream to a bitmap
            try (Image sceneImage = frame.acquireCameraImage()) {
                Log.d(TAG, String.format("Acquired scene image [%d, %d] in format %s",
                        sceneImage.getHeight(), sceneImage.getWidth(), sceneImage.getFormat()));

                // The scene image should and must be in given format as per documentation.
                if (sceneImage.getFormat() != ImageFormat.YUV_420_888) {
                    throw new UnsupportedOperationException(
                            String.format("Cannot process scene image in format %s", sceneImage.getFormat()));
                }

                // Pass to model.
                modelExecutor.run(sceneImage);
            } catch (NotYetAvailableException e) {
                Log.e(TAG, "Could not get scene image.");
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, e.getMessage());
            }
        }




//        Optional<ViewRenderable> menu = arActivity.getMenuRenderable();
//        if (!menu.isPresent()) {
//            return;
//        }
//
//        Anchor anchor = hitResult.createAnchor();
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        anchorNode.setParent(arFragment.getArSceneView().getScene());
//
//        Node menuNode = new Node();
//        menuNode.setParent(anchorNode);
//        menuNode.setRenderable(menu.get());
    }
}
