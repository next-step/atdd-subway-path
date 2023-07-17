package subway.dto.response;

import lombok.Builder;
import subway.entity.Station;

public class StationResponse {
    private Long id;
    private String name;

    @Builder
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


    public static StationResponse from(Station station) {
        return StationResponse.builder()
            .id(station.getId())
            .name(station.getName())
            .build();
    }
}
