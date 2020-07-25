package nextstep.subway.map.domain;

import nextstep.subway.line.domain.LineStation;

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

    public double getWeight(LineStation it) {
        if (this.getType().equals("DURATION")) {
            return it.getDuration();
        }
        return it.getDistance();
    }
}
