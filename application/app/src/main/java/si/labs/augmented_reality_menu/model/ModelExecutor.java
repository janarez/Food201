package si.labs.augmented_reality_menu.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ModelExecutor {
    private Context context;

    // Runs the tf-lite model.
    private Interpreter interpreter;

    // Model constants.
    private static final String MODEL_NAME = "apple_barbecue.tflite";
    private static final int IMAGE_HEIGHT = 64;
    private static final int IMAGE_WIDTH = 64;
    private static final int CLASSES = 208;

    // Testing variables for model timing.
    private long totalTime = 0;
    private long preprocessTime = 0;
    private long segmentationTime = 0;
    private long maskTime = 0;

    private static final String TAG = ModelExecutor.class.getSimpleName();

    public ModelExecutor(Context context) {
        this.context = context;
        interpreter = getInterpreter(context, MODEL_NAME);
    }

    public Bitmap run(Image image) {
        totalTime = System.currentTimeMillis();
        // Image preprocessing.
        preprocessTime = System.currentTimeMillis();
        ByteBuffer byteBuffer = preprocessImage(image);
        preprocessTime -= System.currentTimeMillis();
        Log.d(TAG, String.format("Image preprocessing took %d ms", preprocessTime));

        // Segmentation.
        segmentationTime = System.currentTimeMillis();

        segmentationTime -= System.currentTimeMillis();
        Log.d(TAG, String.format("Segmentation took %d ms", segmentationTime));

        // Mask processing.
        maskTime = System.currentTimeMillis();
        maskTime -= System.currentTimeMillis();
        Log.d(TAG, String.format("Mask postprocessing took %d ms", maskTime));

        totalTime -= System.currentTimeMillis();
        Log.d(TAG, String.format("Full model run took %d ms", totalTime));

        return null;
    }

    /**
     * Converts AR scene image in YUV format to RGB bitmap and using
     * tf support library resizes and converts to tensor.
     */
    private ByteBuffer preprocessImage(Image image) {
        Bitmap bitmap = ImageUtils.fromYuvImageToRgbBitmap(image);

        // Create ImageProcessor with all preprocessing steps chained.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        // Just resizing, [0,1] transform is model layer.
                        .add(new ResizeOp(IMAGE_HEIGHT, IMAGE_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
                        // TODO: Add rotation (from landscape)? Or not necessary?
                        .build();

        // Push bitmap to TensorImage to apply processing.
        TensorImage tImage = new TensorImage(DataType.UINT8);
        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage);
        return tImage.getBuffer();
    }

    /**
     * Initializes model interpreter with the tf-lite model.
     */
    private Interpreter getInterpreter(Context context, String modelName) {
        Interpreter.Options interpreterOptions = new Interpreter.Options();
        // TODO: Setup thread utilization and possible GPU.
        MappedByteBuffer modelFile = getModelFile(context, modelName);
        return new Interpreter(modelFile, interpreterOptions);
    }

    /**
     * Loads model from assets.
     */
    private MappedByteBuffer getModelFile(Context context, String modelName) {
        try {
            AssetFileDescriptor fD = context.getAssets().openFd(modelName);
            FileInputStream inputStream = new FileInputStream(fD.getFileDescriptor());
            FileChannel inputChannel = inputStream.getChannel();
            MappedByteBuffer modelFile = inputChannel.map(FileChannel.MapMode.READ_ONLY, fD.getStartOffset(), fD.getDeclaredLength());
            fD.close();
            return modelFile;

        } catch (IOException e) {
            Log.e(TAG, String.format("Couldn't find %s among assets.", modelName));
        }
        return null;
    }
}
