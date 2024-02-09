package nextstep.subway.line.section;

public enum ApplyType {
    FIRST, LAST, MIDDLE;

    public boolean isApplyMiddle() {
        return this == MIDDLE;
    }
}
