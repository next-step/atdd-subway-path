package nextstep.subway.station.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationRequest {
    private String name;

    public StationRequest(String name) {
        this.name = name;
    }
}
