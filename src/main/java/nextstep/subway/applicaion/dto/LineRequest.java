package nextstep.subway.applicaion.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @Min(value = 1, message = "길이는 1 이상만 가능합니다.")
    private int distance;

    public LineRequest() {
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
