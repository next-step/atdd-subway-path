package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineStationResponse;

import java.util.function.Function;

public enum PathFindType {
    DISTANCE(LineStationResponse::getDistance),
    DURATION(LineStationResponse::getDuration),
    ;

    private final Function<LineStationResponse, Integer> weightCalculator;


    PathFindType(Function<LineStationResponse, Integer> weightCalculator) {
        this.weightCalculator = weightCalculator;
    }

    public int calculateWeight(LineStationResponse lineStationResponse) {
        return this.weightCalculator.apply(lineStationResponse);
    }
}
