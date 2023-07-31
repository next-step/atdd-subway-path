package nextstep.subway.station.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.station.entity.Station;

@Builder
@Getter
public class StationResponse {

    private Long id;

    private String name;

    public static StationResponse of(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }

}
