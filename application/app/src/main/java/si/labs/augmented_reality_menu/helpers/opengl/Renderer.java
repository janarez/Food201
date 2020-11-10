package si.labs.augmented_reality_menu.helpers.opengl;

import android.opengl.GLSurfaceView;

public interface Renderer {
    void onSurfaceCreated(BackgroundRenderer render);
    void onSurfaceChanged(BackgroundRenderer render, int width, int height);
    void onDrawFrame(BackgroundRenderer render);
}
