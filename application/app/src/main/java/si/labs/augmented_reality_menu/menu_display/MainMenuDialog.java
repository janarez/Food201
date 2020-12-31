package si.labs.augmented_reality_menu.menu_display;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import si.labs.augmented_reality_menu.HistoryActivity;
import si.labs.augmented_reality_menu.R;
import si.labs.augmented_reality_menu.persistence.AppDatabase;
import si.labs.augmented_reality_menu.persistence.FoodHistory;
import si.labs.augmented_reality_menu.persistence.FoodHistoryDao;

public class MainMenuDialog extends Dialog {

    private static final String TAG = MainMenuDialog.class.getSimpleName();
    private final Context context;
    private final MenuItemListAdapter listAdapter;
    private FoodHistoryDao foodHistoryDao;

    public MainMenuDialog(@NonNull Context context, MenuItemListAdapter listAdapter) {
        super(context);
        this.context = context;
        this.listAdapter = listAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ar_activity_main_menu_popup);

        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "food-db").build();
        foodHistoryDao = db.foodHistoryDao();

        Button saveButton = findViewById(R.id.main_menu_save_labels_button);
        saveButton.setOnClickListener(v -> saveFoodHistory());

        Button openHistoryMenuButton = findViewById(R.id.main_menu_view_history_button);
        openHistoryMenuButton.setOnClickListener(v -> openHistoryMenu());
    }

    private void openHistoryMenu() {
        Intent openHistoryActivityIntent = new Intent(context, HistoryActivity.class);
        context.startActivity(openHistoryActivityIntent);
    }

    private void saveFoodHistory() {
        List<MenuValueHolder> detectedLabels = listAdapter.getValues();
        String concatenatedLabels = detectedLabels.stream().map(MenuValueHolder::getLabel).collect(Collectors.joining(", "));
        FoodHistory foodHistory = new FoodHistory();
        foodHistory.setConsumedFood(concatenatedLabels);
        foodHistory.setTimeOfSaving(new Date());

        CompletableFuture<Void> makeToast = new CompletableFuture<>();

        Executors.newSingleThreadExecutor().execute(() -> {
            foodHistoryDao.insertAll(foodHistory);
            makeToast.complete(null);
        });

        makeToast.handle((aVoid, throwable) -> {
            if (throwable == null) {
                Toast.makeText(context, "Saved successfully", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, throwable.getMessage());
            }
            return null;
        });
    }
}
