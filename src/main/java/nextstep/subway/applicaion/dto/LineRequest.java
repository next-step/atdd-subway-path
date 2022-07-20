package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {
    private String name;
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @Positive
    private int distance;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return Line.of(name, color);
    }
}
