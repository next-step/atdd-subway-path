package nextstep.subway.domain;

public enum SectionUpdatePosition {

    FIRST("first"),
    MIDDLE("middle"),
    END("end"),
    UNKNOWN("unknown");

    private final String position;

    SectionUpdatePosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}
