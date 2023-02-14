package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import java.util.Objects;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line toEntity() {
        return new Line(this.name, this.color);
    }

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

    public boolean createNewSection() {
        return !Objects.isNull(upStationId) && !Objects.isNull(downStationId);
    }
}
