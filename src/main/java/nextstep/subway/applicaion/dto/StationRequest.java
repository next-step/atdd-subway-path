package nextstep.subway.applicaion.dto;

public class StationRequest {
    private String name;

    public StationRequest(String name) {
        this.name = name;
    }

    public StationRequest() {
    }

    public String getName() {
        return name;
    }
}
