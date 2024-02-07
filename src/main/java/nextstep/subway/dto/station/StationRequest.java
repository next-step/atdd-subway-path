package nextstep.subway.dto.station;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public StationRequest() {}
}
