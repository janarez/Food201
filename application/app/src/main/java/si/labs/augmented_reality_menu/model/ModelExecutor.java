package si.labs.augmented_reality_menu.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import si.labs.augmented_reality_menu.ARActivity;

public class ModelExecutor {
    private Context context;

    // Runs the tf-lite model.
    private Interpreter interpreter;

    // Testing variables for model timing.
    private long totalTime = 0;
    private long preprocessingTime = 0;
    private long segmentationTime = 0;
    private long maskFlatteningTime = 0;

    private static final String TAG = ModelExecutor.class.getSimpleName();

    ModelExecutor(Context context, String modelName) {
        this.context = context;
        interpreter = getInterpreter(context, modelName);
    }



    // Initializes model interpreter with the tf-lite model.
    private Interpreter getInterpreter(Context context, String modelName) {
        Interpreter.Options interpreterOptions = new Interpreter.Options();
        // TODO: Setup thread utilization and possible GPU.
        MappedByteBuffer modelFile = getModelFile(context, modelName);
        return new Interpreter(modelFile, interpreterOptions);
    }

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


