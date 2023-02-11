package nextstep.subway.domain;

public enum InsertLocation {
    PREV_HEAD,
    NEXT_TAIL,

    NEXT_HEAD,
    PREV_TAIL;

    public boolean isBetween() {
        return this == NEXT_HEAD
                || this == PREV_TAIL;
    }

    public boolean isNextHead() {
        return this == NEXT_HEAD;
    }

    public boolean isPrevTail() {
        return this == PREV_TAIL;
    }
}
