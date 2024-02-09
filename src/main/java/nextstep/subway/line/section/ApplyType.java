package nextstep.subway.line.section;

public enum ApplyType {
    ADD_FIRST, ADD_LAST, ADD_MIDDLE, DELETE_FIRST, DELETE_LAST, DELETE_MIDDLE;

    public boolean isApplyMiddle() {
        return this == ADD_MIDDLE || this == DELETE_MIDDLE;
    }
}
