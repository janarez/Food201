package si.labs.augmented_reality_menu.menu_display;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import androidx.annotation.NonNull;

import si.labs.augmented_reality_menu.R;

public class LabelMenuDialog extends Dialog {
    private final MenuItemListAdapter listAdapter;

    public LabelMenuDialog(@NonNull Context context, MenuItemListAdapter listAdapter) {
        super(context);

        this.listAdapter = listAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu_dialog_label_popup);

        ListView labelsList = findViewById(R.id.menu_popup_ar_label);
        labelsList.setAdapter(listAdapter);
    }
}
