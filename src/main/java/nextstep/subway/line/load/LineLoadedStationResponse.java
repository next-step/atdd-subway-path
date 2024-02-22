package nextstep.subway.line.load;

import nextstep.subway.station.Station;

public class LineLoadedStationResponse {

    private final Long id;
    private final String name;

    public LineLoadedStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static LineLoadedStationResponse from(Station station) {
        return new LineLoadedStationResponse(station.getId(), station.getName());
    }
}
