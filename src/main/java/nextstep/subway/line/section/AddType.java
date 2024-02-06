package nextstep.subway.line.section;

public enum AddType {
    FIRST, LAST, MIDDLE;

    public boolean isDistanceChange() {
        return this != MIDDLE;
    }
}
