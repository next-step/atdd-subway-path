package nextstep.subway.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Station;

@Getter
@RequiredArgsConstructor
public final class StationResponse {
    private final Long id;
    private final String name;

    public static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
