package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Station;

@NoArgsConstructor
@Getter
public class StationResponse {
    private Long id;
    private String name;

    @Builder
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return StationResponse.builder()
            .id(station.getId())
            .name(station.getName())
            .build();
    }
}
