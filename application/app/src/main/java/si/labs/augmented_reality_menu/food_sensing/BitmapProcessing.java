package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;

public final class BitmapProcessing {

    private Bitmap getEdges(Bitmap bitmap) {
        Bitmap edges = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int currentPixel = bitmap.getPixel(i, j);

                if (i == 0 || j == 0 || i == bitmap.getHeight() - 1 || j == bitmap.getWidth() - 1) {
                    // picture edges are always edges
                    edges.setPixel(i, j, 1);
                } else if (bitmap.getPixel(i + 1, j) != currentPixel ||
                        bitmap.getPixel(i, j + 1) != currentPixel) {
                    edges.setPixel(i, j, 1);
                } else {
                    edges.setPixel(i, j, 0);
                }
            }
        }

        return edges;
    }
}
