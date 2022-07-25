package nextstep.subway.station.applicaion.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class StationRequest {
    @NotBlank
    private String name;
}
