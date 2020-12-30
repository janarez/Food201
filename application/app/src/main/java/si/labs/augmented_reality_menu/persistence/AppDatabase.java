package si.labs.augmented_reality_menu.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FoodHistory.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FoodHistory foodHistory();
}
