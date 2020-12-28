package si.labs.augmented_reality_menu.food_sensing.dto;

import com.google.ar.core.HitResult;

public class FrameHitDataDto {
    private final int centerXPosition;
    private final int centerYPosition;
    private final HitResult centralPointHit;

    public FrameHitDataDto(int centerXPosition, int centerYPosition, HitResult centralPointHit) {
        this.centerXPosition = centerXPosition;
        this.centerYPosition = centerYPosition;
        this.centralPointHit = centralPointHit;
    }

    public int getCenterXPosition() {
        return centerXPosition;
    }

    public int getCenterYPosition() {
        return centerYPosition;
    }

    public HitResult getCentralPointHit() {
        return centralPointHit;
    }
}
