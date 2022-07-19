package nextstep.subway.applicaion.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
public class LineRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @Min(1)
    private Long upStationId;
    @Min(1)
    private Long downStationId;
    @Min(0)
    private int distance;
}
