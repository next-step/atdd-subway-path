package nextstep.subway.applicaion.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {

    @NotNull(message = "name must be a not null")
    private String name;

    @NotNull(message = "color id must be a not null")
    private String color;

    @NotNull(message = "upStation id must be a not null")
    private Long upStationId;

    @NotNull(message = "downStation id must be a not null")
    private Long downStationId;

    @Positive(message = "distance must be a positive value")
    private int distance;

    public LineRequest() {}

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
}