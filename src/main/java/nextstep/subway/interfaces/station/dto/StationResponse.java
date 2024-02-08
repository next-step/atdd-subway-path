package nextstep.subway.interfaces.station.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.station.StationInfo;

@Getter
@RequiredArgsConstructor
public class StationResponse {
    private final Long id;
    private final String name;

    public static StationResponse from(StationInfo.Main info) {
        return new StationResponse(info.getId(), info.getName());
    }
}
