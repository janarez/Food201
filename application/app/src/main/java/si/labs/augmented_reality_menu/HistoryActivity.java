package si.labs.augmented_reality_menu;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.LinkedList;
import java.util.concurrent.Executors;

import si.labs.augmented_reality_menu.history.HistoryAdapter;
import si.labs.augmented_reality_menu.persistence.AppDatabase;
import si.labs.augmented_reality_menu.persistence.FoodHistoryDao;

public class HistoryActivity extends AppCompatActivity {

    private FoodHistoryDao foodHistoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-db").build();
        foodHistoryDao = db.foodHistoryDao();

        HistoryAdapter adapter = new HistoryAdapter(this, 0, new LinkedList<>());
        ListView history = findViewById(R.id.history_list);
        history.setAdapter(adapter);

        updateFoodHistory(adapter);
    }

    private void updateFoodHistory(HistoryAdapter historyAdapter) {
        Executors.newSingleThreadExecutor().execute(() -> historyAdapter.addAll(foodHistoryDao.getAll()));
    }
}