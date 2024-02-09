package nextstep.subway.line.section;

public enum ApplyType {
    ADD_FIRST, ADD_LAST, ADD_MIDDLE, DELETE_FIRST, DELETE_LAST, DELETE_MIDDLE;

    public boolean isApplyMiddle() {
        return this == ApplyType.ADD_MIDDLE || this == ApplyType.DELETE_MIDDLE;
    }

    public boolean isAppyStart() {
        return this == ApplyType.ADD_FIRST || this == ApplyType.DELETE_FIRST;
    }
}
