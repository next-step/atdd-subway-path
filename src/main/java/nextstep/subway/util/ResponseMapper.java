package nextstep.subway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseMapper {
    public static LineResponse from(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                from(line.getStations())
        );
    }

    public static List<StationResponse> from(List<Station> station) {
        return station.stream()
                .map(ResponseMapper::from)
                .collect(Collectors.toList());
    }

    public static StationResponse from(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
