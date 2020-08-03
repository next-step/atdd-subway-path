package nextstep.subway.map.dto;

public enum SearchType {
    DISTANCE("DISTANCE"), DURATION("DURATION");

    String value;

    SearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
