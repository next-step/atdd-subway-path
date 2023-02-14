package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.SectionRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.vo.PathFinder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PathService {
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        PathFinder pathFinder = new PathFinder(stationRepository.findAll(), sectionRepository.findAll());
        Station source = stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 SourceId 입니다."));
        Station target = stationRepository.findById(targetStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 SourceId 입니다."));
        return pathFinder.findPath(source, target);
    }
}
