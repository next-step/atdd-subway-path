package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

@Component
public class LineMapper {

    public LineResponse toResponseFrom(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line.getStations())
        );
    }

    private List<StationResponse> createStationResponses(final List<Station> stations) {
        return stations.stream()
                .map(station -> StationResponse.by(station.getId(), station.getName()))
                .collect(Collectors.toUnmodifiableList());
    }
}
