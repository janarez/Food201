package si.labs.augmented_reality_menu.helpers.opengl;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BackgroundRenderer {
    public void configureSurfaceView(GLSurfaceView glSurfaceView) {
        glSurfaceView.setPreserveEGLContextOnPause(true);
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(new GLSurfaceViewRenderer());
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setWillNotDraw(false);
    }
}
