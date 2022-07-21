package nextstep.subway.line.application.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
public class LineRequest {
    @NotBlank(message = "노선의 이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "노선의 색 정보를 입력해주세요.")
    private String color;
    @Min(1)
    private Long upStationId;
    @Min(1)
    private Long downStationId;
    @Min(0)
    private int distance;
}
