package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import java.util.Objects;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
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

    public Line toEntity() {
        return new Line(name, color);
    }

    public boolean hasUpStationAndDownStation() {
        return Objects.nonNull(upStationId) &&
                Objects.nonNull(downStationId) &&
                distance > 0;
    }
}
