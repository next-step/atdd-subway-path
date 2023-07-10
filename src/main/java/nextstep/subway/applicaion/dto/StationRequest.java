package nextstep.subway.applicaion.dto;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }
}
