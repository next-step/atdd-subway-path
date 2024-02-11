package nextstep.subway.station.presentation.response;

import nextstep.subway.station.domain.Station;

public class CreateStationResponse {

    private Long stationId;
    private String name;

    public CreateStationResponse(Long stationId, String name) {
        this.stationId = stationId;
        this.name = name;
    }

    public static CreateStationResponse from(Station station) {
        return new CreateStationResponse(
                station.getStationId(),
                station.getName()
        );
    }

    public Long getStationId() {
        return stationId;
    }

    public String getName() {
        return name;
    }

}
