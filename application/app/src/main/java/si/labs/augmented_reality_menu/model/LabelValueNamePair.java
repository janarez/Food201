package si.labs.augmented_reality_menu.model;

public class LabelValueNamePair {
    private String labelName;
    private int labelValue;

    public LabelValueNamePair(String labelName, int labelValue) {
        this.labelName = labelName;
        this.labelValue = labelValue;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public int getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(int labelValue) {
        this.labelValue = labelValue;
    }
}
