package nextstep.subway.path.dto;

public enum PathType {
    DISTANCE, DURATION;

    public boolean isDuration() {
        return DURATION == this;
    }
    public boolean isDistance() {
        return DISTANCE == this;
    }
}
