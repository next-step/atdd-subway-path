package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.Station;

@Getter
@NoArgsConstructor
public class PathStationResponse {
    private Long id;
    private String name;

    public PathStationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }
}
