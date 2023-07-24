package nextstep.subway.service;

import nextstep.subway.common.NotFoundStationException;
import nextstep.subway.controller.resonse.PathResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.vo.Path;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PathFindService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathFindService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse getShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new NotFoundStationException(sourceStationId));
        Station targetStation = stationRepository.findById(targetStationId)
                .orElseThrow(() -> new NotFoundStationException(targetStationId));

        Path shortestPath = getShortestPath(sourceStation, targetStation);

        return new PathResponse(
                shortestPath.getStations().stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList()),
                shortestPath.getDistance()
        );

    }

    private Path getShortestPath(Station sourceStation, Station targetStation) {
        List<Section> sections = lineRepository.findAll().stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return new PathFinder(sections).getShortestPath(sourceStation, targetStation);
    }
}
