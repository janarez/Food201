package si.labs.augmented_reality_menu.menu_display;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import si.labs.augmented_reality_menu.R;

public class MainMenuDialog extends Dialog {
    public MainMenuDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ar_activity_main_menu_popup);
    }
}
