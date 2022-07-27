package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    private StationResponse() {
    }

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(
            station.getId(),
            station.getName()
        );
    }
}
