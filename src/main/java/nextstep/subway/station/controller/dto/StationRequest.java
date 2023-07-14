package nextstep.subway.station.controller.dto;

public class StationRequest {
    private String name;

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
