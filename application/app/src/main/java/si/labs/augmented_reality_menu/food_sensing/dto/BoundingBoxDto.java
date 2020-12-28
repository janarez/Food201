package si.labs.augmented_reality_menu.food_sensing.dto;

public class BoundingBoxDto {
    private final int classOfInterest;

    private int XMin;
    private int XMax;
    private int YMin;
    private int YMax;

    public BoundingBoxDto(int classOfInterest) {
        this.classOfInterest = classOfInterest;
    }

    public int getClassOfInterest() {
        return classOfInterest;
    }

    public int getXMin() {
        return XMin;
    }

    public void setXMin(int XMin) {
        this.XMin = XMin;
    }

    public int getXMax() {
        return XMax;
    }

    public void setXMax(int XMax) {
        this.XMax = XMax;
    }

    public int getYMin() {
        return YMin;
    }

    public void setYMin(int YMin) {
        this.YMin = YMin;
    }

    public int getYMax() {
        return YMax;
    }

    public void setYMax(int YMax) {
        this.YMax = YMax;
    }
}
