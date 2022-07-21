package nextstep.subway.station.applicaion.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class StationRequest {
    @NotBlank
    private String name;
}
