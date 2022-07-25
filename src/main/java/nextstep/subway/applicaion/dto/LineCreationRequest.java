package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
public class LineCreationRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @NotNull
    @Positive
    private Long upStationId;

    @NotNull
    @Positive
    private Long downStationId;

    @Positive
    private int distance;

}
