package nextstep.subway.station.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.station.entity.Station;

@Getter
@Setter
@NoArgsConstructor
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
