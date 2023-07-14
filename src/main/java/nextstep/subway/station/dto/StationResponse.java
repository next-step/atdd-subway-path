package nextstep.subway.station.dto;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(StationDto dto) {
        return new StationResponse(dto.getId(), dto.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
