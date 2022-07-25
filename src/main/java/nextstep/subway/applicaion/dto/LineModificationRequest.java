package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class LineModificationRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

}
