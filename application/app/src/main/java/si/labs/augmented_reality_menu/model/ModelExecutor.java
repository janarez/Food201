package si.labs.augmented_reality_menu.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.HashSet;

public class ModelExecutor {
    private Context context;

    // Runs the tf-lite model.
    private Interpreter interpreter;

    // Model constants.
    private static final String MODEL_NAME = "apple_barbecue.tflite";
    private static final int IMAGE_HEIGHT = 64;
    private static final int IMAGE_WIDTH = 64;
    private static final int CLASSES = 209;

    // Stores random color for each label.
    private int[] labelColors = new int[CLASSES];

    // Testing variables for model timing.
    private long totalTime = 0;
    private long preprocessTime = 0;
    private long segmentationTime = 0;
    private long maskTime = 0;

    private static final String TAG = ModelExecutor.class.getSimpleName();

    public ModelExecutor(Context context) {
        this.context = context;
        interpreter = getInterpreter(context);

        // Initialize label colors.
        labelColors[0] = 0; // Transparent.
        for (int l = 1; l < CLASSES; l++) {
            labelColors[l] = ImageUtils.getRandomColorInt();
        }
        Log.d(TAG, "Created `ModelExecutor` instance.");
    }

    public Bitmap run(Image image) {
        totalTime = System.currentTimeMillis();

        // Image preprocessing.
        preprocessTime = System.currentTimeMillis();
        // Save original dimensions for resizing mask.
        int originalHeight = image.getHeight();
        int originalWidth = image.getWidth();
        Bitmap inputBitmap = ImageUtils.fromYuvImageToRgbBitmap(image);
        ByteBuffer inputBuffer = preprocessImage(inputBitmap);
        preprocessTime = System.currentTimeMillis() - preprocessTime;
        Log.d(TAG, String.format("Image preprocessing took %d ms", preprocessTime));

        // Segmentation.
        segmentationTime = System.currentTimeMillis();
        // Preallocate space for output mask.
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(IMAGE_HEIGHT * IMAGE_WIDTH * CLASSES * 4); // Int32.
        outputBuffer.order(ByteOrder.nativeOrder()); // To spare extraneous copying.
        interpreter.run(inputBuffer, outputBuffer);
        segmentationTime = System.currentTimeMillis() - segmentationTime;
        Log.d(TAG, String.format("Segmentation took %d ms", segmentationTime));

        // Mask processing.
        maskTime = System.currentTimeMillis();
        Bitmap maskOverlay = postprocessMask(outputBuffer, inputBitmap, originalHeight, originalWidth);
        maskTime = System.currentTimeMillis() - maskTime;
        Log.d(TAG, String.format("Mask postprocessing took %d ms", maskTime));
        totalTime = System.currentTimeMillis() - totalTime;
        Log.d(TAG, String.format("Full model run took %d ms", totalTime));

        return maskOverlay;
    }

    /**
     * Transforms model's output `ByteBuffer` to `Bitmap` of specified size.
     * The predicted mask is overlayed on `inputBitmap`.
     */
    private Bitmap postprocessMask(ByteBuffer outputBuffer, Bitmap inputBitmap, int height, int width) {
        Bitmap mask = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        Bitmap maskOverlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // TODO: Return labels as well for AR.
        HashSet<Integer> labels = new HashSet<>();

        // Iterate over buffer to extract label at each pixel.
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                int label = outputBuffer.getInt((y * IMAGE_WIDTH + x) * 4);
                labels.add(label);
                mask.setPixel(x, y, labelColors[label]);
            }
        }
        // Resize to original image size.
        Bitmap enlargedMask = Bitmap.createScaledBitmap(mask, width, height, true);

        // Overlay input image and predicted mask.
        Canvas canvas = new Canvas(maskOverlay);
        canvas.drawBitmap(inputBitmap, new Matrix(), null);
        canvas.drawBitmap(enlargedMask, new Matrix(), null);
        return maskOverlay;
    }

    /**
     * Converts AR scene image in YUV format to RGB bitmap and using
     * tf support library resizes and converts to tensor.
     */
    private ByteBuffer preprocessImage(Bitmap bitmap) {
        // Create ImageProcessor with all preprocessing steps chained.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        // Just resizing, [0,1] transform is model layer.
                        .add(new ResizeOp(IMAGE_HEIGHT, IMAGE_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
                        // TODO: Add rotation (from landscape)? Or not necessary?
                        .build();

        // Push bitmap to TensorImage to apply processing.
        TensorImage tImage = new TensorImage(DataType.FLOAT32);
        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage);
        return tImage.getBuffer();
    }

    /**
     * Initializes model interpreter with the tf-lite model.
     */
    private Interpreter getInterpreter(Context context) {
        Interpreter.Options interpreterOptions = new Interpreter.Options();
        // TODO: Setup thread utilization and possible GPU.
        MappedByteBuffer modelFile = getModelFile(context);

        // Will crash if `modelFile` is null.
        return new Interpreter(modelFile, interpreterOptions);
    }

    /**
     * Loads model from assets.
     */
    private MappedByteBuffer getModelFile(Context context) {
        try {
            return FileUtil.loadMappedFile(context, MODEL_NAME);
        } catch (IOException e) {
            Log.e(TAG, String.format("Couldn't find %s among assets.", MODEL_NAME));
        }
        return null;
    }
}
