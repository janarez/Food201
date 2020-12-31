package si.labs.augmented_reality_menu.menu_display;

import java.util.Objects;

public class MenuValueHolder {
    private final String label;
    private final int labelValue;

    public MenuValueHolder(String label, int labelValue) {
        this.label = label;
        this.labelValue = labelValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuValueHolder that = (MenuValueHolder) o;
        return labelValue == that.labelValue &&
                label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, labelValue);
    }

    public String getLabel() {
        return label;
    }

    public int getLabelValue() {
        return labelValue;
    }
}
