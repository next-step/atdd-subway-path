package nextstep.subway.station.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.station.entity.Station;

@Builder
@Getter
public class StationResponseDto {

    private Long id;

    private String name;

    public static StationResponseDto of(Station station) {
        return StationResponseDto.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }

}
