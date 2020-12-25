package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;

public final class BitmapProcessing {

    public Bitmap getEdges(Bitmap bitmap) {
        Bitmap edges = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int currentPixel = bitmap.getPixel(j, i);

                if (j == 0 || i == 0 || i == bitmap.getHeight() - 1 || j == bitmap.getWidth() - 1) {
                    // picture edges are never edges
                    edges.setPixel(j, i, 0);
                } else if (bitmap.getPixel(j + 1, i) != currentPixel ||
                        bitmap.getPixel(j, i + 1) != currentPixel) {
                    edges.setPixel(j, i, -14937547);
                } else {
                    edges.setPixel(j, i, 0);
                }
            }
        }

        return edges;
    }
}
