package nextstep.subway.controller.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class LineCreateRequest {
    @Size(max = 20)
    @NotEmpty
    private String name;
    @Size(max = 20)
    @NotEmpty
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private Long distance;
}
