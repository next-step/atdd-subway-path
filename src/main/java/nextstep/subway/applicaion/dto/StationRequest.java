package nextstep.subway.applicaion.dto;

public class StationRequest {

    private String name;

    public static StationRequest of(String name) {
        StationRequest request = new StationRequest();
        request.name = name;

        return request;
    }

    public String getName() {
        return name;
    }

}
