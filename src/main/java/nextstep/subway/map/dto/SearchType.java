package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineStationResponse;

import java.util.function.Function;

public enum SearchType {
    DISTANCE("DISTANCE", lineStation -> lineStation.getDistance()),
    DURATION("DURATION", lineStation -> lineStation.getDuration());

    private final String weight;
    private final Function<LineStationResponse, Integer> expression;

    SearchType(String weight, Function<LineStationResponse, Integer> expression) {
        this.weight = weight;
        this.expression = expression;
    }

    public int match(LineStationResponse lineStation) {
        return expression.apply(lineStation);
    }

    public String getWeight() {
        return weight;
    }
}
