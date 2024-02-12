package nextstep.subway.station.presentation.request;

public class CreateStationRequest {
    private String name;

    private CreateStationRequest() {
    }

    private CreateStationRequest(String name) {
        this.name = name;
    }

    public static CreateStationRequest from(String name) {
        return new CreateStationRequest(name);
    }

    public String getName() {
        return name;
    }

}
