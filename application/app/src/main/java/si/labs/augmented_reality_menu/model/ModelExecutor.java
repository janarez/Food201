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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ModelExecutor {
    private Context context;

    // Runs the tf-lite model.
    private Interpreter interpreter;

    // Model constants.
    private static final String MODEL_FILE = "hamburger_hummus_01.tflite";
    private static final String LABEL_FILE = "labels.txt";

    private static final int IMAGE_HEIGHT = 64;
    private static final int IMAGE_WIDTH = 64;
    private static final int CLASSES = 209;

    // Stores random color for each label.
    private int[] labelColors = new int[CLASSES];
    private List<String> labels;

    private static final String TAG = ModelExecutor.class.getSimpleName();

    public ModelExecutor(Context context) {
        this.context = context;
        interpreter = getInterpreter(context);
        labels = getLabels(context);

        // Initialize label colors.
        labelColors[0] = 0; // Transparent.
        for (int l = 1; l < CLASSES; l++) {
            labelColors[l] = ImageUtils.getRandomColorInt();
        }
        Log.d(TAG, "Created `ModelExecutor` instance.");
    }

    /**
     * Runs segmentation model and returns `ModelOutput` with overlayed mask on original image
     * as well as found labels.
     */
    public ModelOutput run(Image image) {
        long totalTime = System.currentTimeMillis();

        // Image preprocessing.
        long preprocessTime = System.currentTimeMillis();
        // Save original dimensions for resizing mask.
        int originalHeight = image.getHeight();
        int originalWidth = image.getWidth();
        Bitmap inputBitmap = ImageUtils.fromYuvImageToRgbBitmap(image);
        ByteBuffer inputBuffer = preprocessImage(inputBitmap);
        preprocessTime = System.currentTimeMillis() - preprocessTime;
        Log.d(TAG, String.format("Image preprocessing took %d ms", preprocessTime));

        // Segmentation.
        long segmentationTime = System.currentTimeMillis();
        // Preallocate space for output mask.
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(IMAGE_HEIGHT * IMAGE_WIDTH * CLASSES * 4); // Int32.
        outputBuffer.order(ByteOrder.nativeOrder()); // To spare extraneous copying.
        interpreter.run(inputBuffer, outputBuffer);
        segmentationTime = System.currentTimeMillis() - segmentationTime;
        Log.d(TAG, String.format("Segmentation took %d ms", segmentationTime));

        // Mask processing.
        long maskTime = System.currentTimeMillis();
        ModelOutput modelOutput = postprocessMask(outputBuffer, inputBitmap, originalHeight, originalWidth);
        maskTime = System.currentTimeMillis() - maskTime;
        Log.d(TAG, String.format("Mask postprocessing took %d ms", maskTime));
        totalTime = System.currentTimeMillis() - totalTime;
        Log.d(TAG, String.format("Full model run took %d ms", totalTime));

        return modelOutput;
    }

    /**
     * Transforms model's output `ByteBuffer` to `Bitmap` of specified size.
     * The predicted mask is overlayed on `inputBitmap`.
     */
    private ModelOutput postprocessMask(ByteBuffer outputBuffer, Bitmap inputBitmap, int height, int width) {
        Bitmap mask = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        Bitmap maskOverlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        HashSet<Integer> idLabels = new HashSet<>();

        // Track winning class at given pixel.
        int maxLabel = 0;
        float maxProb = 0;

        // Iterate over buffer to extract label at each pixel.
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                for (int c = 0; c < CLASSES; c++) {
                    float prob = outputBuffer.getFloat(
                            (y * IMAGE_WIDTH * CLASSES + x * CLASSES + c) * 4);
                    if (prob > maxProb) {
                        maxProb = prob;
                        maxLabel = c;
                    }
                    // Break early if possible.
                    if (maxProb > 0.5) {
                        break;
                    }
                }
                if (idLabels.add(maxLabel)) {
                    Log.d(TAG, String.format("Found another label: %d.", maxLabel));
                }
                mask.setPixel(x, y, labelColors[maxLabel]);

                // Reset.
                maxLabel = 0;
                maxProb = 0;
            }
        }
        // Convert class ID's to text labels.
        List<String> labels = convertLabels(idLabels);
        
        // Resize to original image size.
        Bitmap enlargedMask = Bitmap.createScaledBitmap(mask, width, height, true);

        // Overlay input image and predicted mask.
        Canvas canvas = new Canvas(maskOverlay);
        canvas.drawBitmap(inputBitmap, new Matrix(), null);
        canvas.drawBitmap(enlargedMask, new Matrix(), null);
        return new ModelOutput(maskOverlay, labels);
    }

    /**
     * Converts id's to string labels.
     */
    private List<String> convertLabels(HashSet<Integer> ids) {
        List<String> strings = new ArrayList<>(ids.size());
        for (int id : ids) {
            strings.add(labels.get(id));
        }
        Log.d(TAG, String.format("Found %d labels in mask.", ids.size()));
        return strings;
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
            return FileUtil.loadMappedFile(context, MODEL_FILE);
        } catch (IOException e) {
            Log.e(TAG, String.format("Couldn't find %s among assets.", MODEL_FILE));
        }
        return null;
    }

    /**
     * Loads labels from assets.
     */
    private List<String> getLabels(Context context) {
        try {
            return FileUtil.loadLabels(context, LABEL_FILE);
        } catch (IOException e) {
            Log.e(TAG, String.format("Couldn't find %s among assets.", LABEL_FILE));
        }
        return null;
    }
}
