package nextstep.subway.domain.station.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.station.domain.Station;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    @Builder
    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }
}
