package nextstep.subway.station;

public class StationRequest {
    private String name;

    protected StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
