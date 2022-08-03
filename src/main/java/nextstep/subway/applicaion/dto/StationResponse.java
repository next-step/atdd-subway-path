package nextstep.subway.applicaion.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
@EqualsAndHashCode
public class StationResponse {
    private Long id;
    private String name;

    private StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
