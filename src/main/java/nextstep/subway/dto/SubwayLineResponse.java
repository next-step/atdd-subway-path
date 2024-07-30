package nextstep.subway.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.SubwayLine;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public final class SubwayLineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final List<StationResponse> stations;

    public static SubwayLineResponse from(SubwayLine subwayLine) {
        List<StationResponse> stationResponses = subwayLine.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());

        return new SubwayLineResponse(
                subwayLine.getId(),
                subwayLine.getName(),
                subwayLine.getColor(),
                subwayLine.getUpStation().getId(),
                subwayLine.getDownStation().getId(),
                stationResponses
        );
    }
}
