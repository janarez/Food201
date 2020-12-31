package si.labs.augmented_reality_menu.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "food_history")
public class FoodHistory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Date timeOfSaving;
    private String consumedFood;

    public FoodHistory() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimeOfSaving() {
        return timeOfSaving;
    }

    public void setTimeOfSaving(Date timeOfSaving) {
        this.timeOfSaving = timeOfSaving;
    }

    public String getConsumedFood() {
        return consumedFood;
    }

    public void setConsumedFood(String consumedFood) {
        this.consumedFood = consumedFood;
    }
}
