package nextstep.subway.applicaion.dto;

import javax.validation.constraints.NotBlank;

public class LineUpdateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
