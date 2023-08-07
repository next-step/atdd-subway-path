package nextstep.subway.applicaion.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {
    @NotBlank(message = "지하철 노선에 이름이 있어야 합니다.")
    private String name;
    private String color;
    @NotNull(message = "지하철 노선에 상행역이 있어야 합니다..")
    private Long upStationId;
    @NotNull(message = "지하철 노선에 하행역이 있어야 합니다.")
    private Long downStationId;
    @Positive(message = "지하철 노선 거리는 양수여야 합니다.")
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
