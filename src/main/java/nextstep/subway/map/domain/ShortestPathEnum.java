package nextstep.subway.map.domain;

public enum ShortestPathEnum {
    DISTANCE("DISTANCE"),
    DURATION("DURATION");

    private final String type;

    ShortestPathEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
