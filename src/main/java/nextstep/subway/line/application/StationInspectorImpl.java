package nextstep.subway.line.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.StationInspector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StationInspectorImpl implements StationInspector {

    private final LineRepository lineRepository;

    @Override
    public boolean belongsToLine(Long stationId) {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .anyMatch(line -> line.getOrderedStationIds().contains(stationId));
    }
}
