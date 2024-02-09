package nextstep.subway.applicaion.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LineCreateRequest {
    private String name;
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;


    @Min(1)
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
}
