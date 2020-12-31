package si.labs.augmented_reality_menu.menu_display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import si.labs.augmented_reality_menu.R;

public class MenuItemListAdapter extends ArrayAdapter<MenuValueHolder> {
    private static final String TAG = MenuItemListAdapter.class.getSimpleName();
    private final Context context;
    private final List<MenuValueHolder> values;

    public MenuItemListAdapter(Context context, int resource, List<MenuValueHolder> menuValueHolders) {
        super(context, resource, menuValueHolders);
        this.context = context;
        this.values = menuValueHolders;
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
        final LabelCheckboxView labelCheckboxView;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.menu_list_item, parent, false);
            labelCheckboxView = new LabelCheckboxView();
            labelCheckboxView.setColourDisplay(convertView.findViewById(R.id.menu_item_colour_check));
            labelCheckboxView.setTextView(convertView.findViewById(R.id.menu_item_text));
            convertView.setTag(labelCheckboxView);
        } else {
            labelCheckboxView = (LabelCheckboxView) convertView.getTag();
        }

        labelCheckboxView.getTextView().setText(values.get(position).getLabel());
        labelCheckboxView.getColourDisplay().setBackgroundColor(values.get(position).getLabelValue());

        return convertView;
    }

    public List<MenuValueHolder> getValues() {
        return values;
    }

    public void clearList() {
        values.clear();
    }

    private static class LabelCheckboxView {
        private TextView textView;
        private TextView colourDisplay;

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public TextView getColourDisplay() {
            return colourDisplay;
        }

        public void setColourDisplay(TextView colourDisplay) {
            this.colourDisplay = colourDisplay;
        }
    }
}
