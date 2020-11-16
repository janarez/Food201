package si.labs.augmented_reality_menu.menu_display;

import android.graphics.ImageFormat;
import android.media.Image;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Optional;

import si.labs.augmented_reality_menu.ARActivity;
import si.labs.augmented_reality_menu.R;
import si.labs.augmented_reality_menu.model.ModelExecutor;
import si.labs.augmented_reality_menu.model.ModelOutput;

public class DisplayOnPlaneTapImpl implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = DisplayOnPlaneTapImpl.class.getSimpleName();

    private final BaseArFragment arFragment;
    private final ARActivity arActivity;
    private final ModelExecutor modelExecutor;
    private TransformableNode menuNode;

    public DisplayOnPlaneTapImpl(ModelExecutor modelExecutor, BaseArFragment arFragment, ARActivity arActivity) {
        this.arFragment = arFragment;
        this.arActivity = arActivity;
        this.modelExecutor = modelExecutor;
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        ModelOutput modelOutput = null;

        // Get camera scene for the model.
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            // Copy the camera stream to a bitmap
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
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        // TODO: Handle labels + mask display differently. For now put labels inside random text box.
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

        // Display the obtained labels.
        TextView textBox = menu.get().getView().findViewById(R.id.orbitHeader);
        textBox.setText(modelOutput != null ? modelOutput.labelsAsSingleString() : "Segmentation failed.");
    }
}
