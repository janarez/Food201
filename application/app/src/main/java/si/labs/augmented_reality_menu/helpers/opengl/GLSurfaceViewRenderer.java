package si.labs.augmented_reality_menu.helpers.opengl;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0, 0,0,0);
        GLES30.glEnable(GLES30.GL_BLEND);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }
}
