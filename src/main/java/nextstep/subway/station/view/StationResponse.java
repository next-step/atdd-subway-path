package nextstep.subway.station.view;

import lombok.Getter;
import lombok.NoArgsConstructor;

import subway.station.domain.Station;

@Getter
@NoArgsConstructor
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
