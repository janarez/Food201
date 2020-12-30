package si.labs.augmented_reality_menu.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "food_history")
public class FoodHistory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Instant timeOfSaving;
    private List<String> consumedFood;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Instant getTimeOfSaving() {
        return timeOfSaving;
    }

    public void setTimeOfSaving(Instant timeOfSaving) {
        this.timeOfSaving = timeOfSaving;
    }

    public List<String> getConsumedFood() {
        if (consumedFood == null) {
            return new ArrayList<>();
        }

        return consumedFood;
    }

    public void setConsumedFood(List<String> consumedFood) {
        this.consumedFood = consumedFood;
    }
}
