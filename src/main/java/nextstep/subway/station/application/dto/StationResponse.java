package nextstep.subway.station.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.station.domain.Station;

@Getter
@AllArgsConstructor
public class StationResponse {

    private Long id;
    private String name;

    public static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
