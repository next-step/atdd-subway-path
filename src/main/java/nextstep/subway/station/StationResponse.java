package nextstep.subway.station;

public class StationResponse {
    private final Long id;
    private final String name;

    public StationResponse(Long id, String name) {
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
