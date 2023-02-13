package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName().getName();
    }
}
