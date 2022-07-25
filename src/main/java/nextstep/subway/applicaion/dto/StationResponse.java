package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

}
