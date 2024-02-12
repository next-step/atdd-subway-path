package nextstep.subway.station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public StationResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
