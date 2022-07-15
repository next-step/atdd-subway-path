package nextstep.subway.applicaion.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LineRequest {
    private String name;
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @Min(value = 1)
    private int distance;

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(name, color);
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
