package si.labs.augmented_reality_menu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import si.labs.augmented_reality_menu.persistence.AppDatabase;

public class HistoryActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "food-db").build();
    }
}