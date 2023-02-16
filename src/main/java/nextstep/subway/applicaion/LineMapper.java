package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import org.springframework.stereotype.Component;

@Component
public class LineMapper {

    private final StationMapper stationMapper;

    public LineMapper(final StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    public LineResponse toResponseFrom(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stationMapper.toResponseFrom(line.getStations())
        );
    }
}
