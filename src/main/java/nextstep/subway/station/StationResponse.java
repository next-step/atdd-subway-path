package nextstep.subway.station;

public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse of (Station station){
        return new StationResponse(station.getId(), station.getName());
    }

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
