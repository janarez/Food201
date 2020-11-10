package si.labs.augmented_reality_menu.helpers.opengl;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.io.IOException;

import si.labs.augmented_reality_menu.helpers.opengl.primitives.Mesh;
import si.labs.augmented_reality_menu.helpers.opengl.primitives.Shader;

public class GeneralRenderer implements Renderer {
    private static final String TAG = GeneralRenderer.class.getSimpleName();

    private final AssetManager assetManager;
    private final GLSurfaceView glSurfaceView;
    private final Session session;

    private DisplayRotationHelper displayRotationHelper;
    private BackgroundRenderer backgroundRenderer;
    private boolean hasSetTextureNames = false;

    public GeneralRenderer(GLSurfaceView glSurfaceView, Session session,
                           AssetManager assetManager, DisplayRotationHelper displayRotationHelper) {
        this.glSurfaceView = glSurfaceView;
        configureSurfaceView(glSurfaceView);

        this.session = session;
        this.assetManager = assetManager;
        this.displayRotationHelper = displayRotationHelper;
    }

    public void configureSurfaceView(GLSurfaceView glSurfaceView) {
        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(new GLSurfaceViewRendererImpl(this));
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setWillNotDraw(false);
    }

    /** Draw a {@link Mesh} with the specified {@link Shader}. */
    public void draw(Mesh mesh, Shader shader) {
        shader.use();
        mesh.draw();
    }

    @Override
    public void onSurfaceCreated() {
        try {
            backgroundRenderer = new BackgroundRenderer(this);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read an asset file", e);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {

        if (session == null) {
            return;
        }

        if (!hasSetTextureNames) {
            session.setCameraTextureNames(new int[] {backgroundRenderer.getTextureId()});
            hasSetTextureNames = true;
        }

        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            backgroundRenderer.draw(this, frame);
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }

    }

    public AssetManager getAssets() {
        return assetManager;
    }
}
