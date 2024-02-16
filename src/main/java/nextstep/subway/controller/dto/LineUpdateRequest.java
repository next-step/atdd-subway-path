package nextstep.subway.controller.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
public class LineUpdateRequest {
    @Size(max = 20)
    @NotEmpty
    private String name;
    @Size(max = 20)
    @NotEmpty
    private String color;
}
