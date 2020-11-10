package si.labs.augmented_reality_menu.helpers.opengl;

public interface Renderer {
    void onSurfaceCreated();
    void onSurfaceChanged(int width, int height);
    void onDrawFrame();
}
