package si.labs.augmented_reality_menu.menu_display;

public class MenuValueHolder {
    private final String label;
    private final int labelValue;

    private boolean selected;

    public MenuValueHolder(String label, int labelValue) {
        this.label = label;
        this.labelValue = labelValue;

        selected = false;
    }

    public String getLabel() {
        return label;
    }

    public int getLabelValue() {
        return labelValue;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
