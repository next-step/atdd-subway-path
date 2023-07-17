package nextstep.subway.station.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StationRequest {

    private Long id;
    private String name;

    public StationRequest(String name) {
        this.name = name;
    }
}
