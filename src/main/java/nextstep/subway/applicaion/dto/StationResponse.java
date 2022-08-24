package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Station;

@NoArgsConstructor
@Getter
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

}
