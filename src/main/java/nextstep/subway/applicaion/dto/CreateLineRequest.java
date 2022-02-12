package nextstep.subway.applicaion.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateLineRequest {

    @NotNull
    private String name;
    @NotNull
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @Min(1)
    private int distance;

    public CreateLineRequest(String name, String color, Long upStationId, Long downStationId,
            int distance) {
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
