package subway.dto.station;

public class StationRequest {
    private String name;

    public StationRequest() {}

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
