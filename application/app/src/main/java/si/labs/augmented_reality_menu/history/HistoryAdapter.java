package si.labs.augmented_reality_menu.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

import si.labs.augmented_reality_menu.R;
import si.labs.augmented_reality_menu.persistence.FoodHistory;

public class HistoryAdapter extends ArrayAdapter<FoodHistory> {
    private final Context context;
    private final List<FoodHistory> foodHistories;

    public HistoryAdapter(Context context, int resource, List<FoodHistory> foodHistories) {
        super(context, resource, foodHistories);
        this.context = context;
        this.foodHistories = foodHistories;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private View getItemView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final HistoryAdapter.HistoryAdapterItem historyItem;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.history_item, parent, false);
            historyItem = new HistoryAdapterItem();
            historyItem.setTextView(convertView.findViewById(R.id.history_history_item));
            convertView.setTag(historyItem);
        } else {
            historyItem = (HistoryAdapterItem) convertView.getTag();
        }

        historyItem.getTextView().setText(formatText(foodHistories.get(position)));

        return convertView;
    }

    private String formatText(FoodHistory foodHistory) {
        return String.format(Locale.ROOT, "%s %s", foodHistory.getTimeOfSaving().toString(), foodHistory.getConsumedFood());
    }

    private static class HistoryAdapterItem {
        private TextView textView;

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }
}
