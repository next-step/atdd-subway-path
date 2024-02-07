package nextstep.subway.line.section;

public enum AddType {
    FIRST, LAST, MIDDLE;

    public boolean isAddMiddle() {
        return this != MIDDLE;
    }
}
