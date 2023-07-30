package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathDto;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.dto.StationDto;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathDto findPath(Long source, Long target) {
        Station startStation = stationRepository.findById(source)
                .orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않습니다."));
        Station endStation = stationRepository.findById(target)
                .orElseThrow(() -> new IllegalArgumentException("도착역이 존재하지 않습니다."));

        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new PathFinder(lines);
        Sections sections = pathFinder.getSections(pathFinder.findPath(startStation, endStation));

        return PathDto.from(sections.getStations().stream()
                .map(StationDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)), sections.getDistance());
    }
}
