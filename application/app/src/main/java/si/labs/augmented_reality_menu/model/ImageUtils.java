package si.labs.augmented_reality_menu.model;

import android.graphics.Bitmap;
import android.media.Image;

import java.nio.ByteBuffer;

import static androidx.core.math.MathUtils.clamp;

public class ImageUtils {

    /**
     * Converts `Image` with `ImageFormat.YUV_420_888` to Bitmap in RGB format.
     * Algorithm adapted from
     * https://blog.minhazav.dev/how-to-convert-yuv-420-sp-android.media.Image-to-Bitmap-or-jpeg/.
     */
    public static Bitmap fromYuvImageToRgbBitmap(Image image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        // To hold RGB pixel values.
        int[] argbArray = new int[imageWidth * imageHeight];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        // We can get UV planes in one go as they are interleaved (stride 2).
        ByteBuffer uvBuffer = image.getPlanes()[1].getBuffer();

        for (int y = 0; y < imageHeight - 2; y++) {
            for (int x = 0; x < imageWidth - 2; x++) {
                int yIndex = y * imageWidth + x;
                int yValue = (yBuffer.get(yIndex) & 255);

                int uvx = x / 2;
                int uvy = y / 2;

                int uIndex = uvy * imageWidth + 2 * uvx;
                int vIndex = uIndex + 1;

                int uValue = (uvBuffer.get(uIndex) & 255) - 128;
                int vValue = (uvBuffer.get(vIndex) & 255) - 128;

                int r = (int) (yValue + 1.370705f * vValue);
                int g = (int) (yValue - (0.698001f * vValue) - (0.337633f * uValue));
                int b = (int) (yValue + 1.732446f * uValue);
                r = clamp(r, 0, 255);
                g = clamp(g, 0, 255);
                b = clamp(b, 0, 255);
                argbArray[yIndex] = (255 << 24) | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
            }
        }
        return Bitmap.createBitmap(argbArray, imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
    }

    /**
     * Returns random AARRGGBB color int with half opacity.
     */
    public static int getRandomColorInt() {
        int a = 255 << 24;
        int r = (int) (255 * Math.random()) << 16;
        int g = (int) (255 * Math.random()) << 8;
        int b = (int) (255 * Math.random());
        return a | r | g | b;
    }
}

