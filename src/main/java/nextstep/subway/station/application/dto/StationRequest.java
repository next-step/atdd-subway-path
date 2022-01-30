package nextstep.subway.station.application.dto;

import lombok.Getter;

@Getter
public class StationRequest {
    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }
}
