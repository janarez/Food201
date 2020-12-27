package si.labs.augmented_reality_menu.food_sensing;

import android.graphics.Bitmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import si.labs.augmented_reality_menu.food_sensing.dto.BoundingBoxDto;

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
                    edges.setPixel(j, i, -14937547);    // can be anything except 0, this one is easy to see while debugging
                } else {
                    edges.setPixel(j, i, 0);
                }
            }
        }

        return edges;
    }

    public Collection<BoundingBoxDto> getBoundingBoxes(Bitmap bitmap, Set<Integer> classesOfInterest) {
        HashMap<Integer, BoundingBoxDto> results = new HashMap<>(classesOfInterest.size());

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int currentPixel = bitmap.getPixel(j, i);

                if (results.containsKey(currentPixel)) {
                    updateBoundingBoxDto(results.get(currentPixel), j, i);
                } else {
                    BoundingBoxDto dto = new BoundingBoxDto(currentPixel);
                    dto.setXMax(j);
                    dto.setXMax(j);
                    dto.setYMax(i);
                    dto.setYMax(i);
                    results.put(currentPixel, dto);
                }
            }
        }

        return results.values();
    }

    private void updateBoundingBoxDto(BoundingBoxDto dto, int newX, int newY) {
        if (newX > dto.getXMax()) {
            dto.setXMax(newX);
        }
        if (newX < dto.getXMin()) {
            dto.setXMin(newX);
        }
        if (newY > dto.getYMax()) {
            dto.setYMax(newY);
        }
        if (newY < dto.getYMin()) {
            dto.setYMin(newY);
        }
    }
}
