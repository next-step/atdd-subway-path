package nextstep.subway.applicaion.dto;

import javax.validation.constraints.NotBlank;

public class LineRequest {
    @NotBlank
    private String name;
    @NotBlank
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
}
