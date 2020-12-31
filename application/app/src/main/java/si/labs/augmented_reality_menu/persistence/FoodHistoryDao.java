package si.labs.augmented_reality_menu.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodHistoryDao {

    @Query("SELECT * FROM food_history")
    List<FoodHistory> getAll();

    @Insert
    void insertAll(FoodHistory... foodHistories);

    @Delete
    void delete(FoodHistory foodHistory);
}
