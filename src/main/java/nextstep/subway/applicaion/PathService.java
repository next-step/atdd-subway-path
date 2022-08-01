package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.facade.PathFinder;
import nextstep.subway.applicaion.strategy.strategy.DijkstraPathFindStrategy;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathResponse findShortPath(PathRequest pathRequest) {
        Station source = findStationBy(pathRequest.getSource());
        Station target = findStationBy(pathRequest.getTarget());

        List<Section> sections = findAllSection();
        List<Station> stations = stationRepository.findAll();

        PathFinder pathFinder = new PathFinder(new DijkstraPathFindStrategy(stations, sections));

        return createResponse(
                pathFinder.findShortestPath(source, target),
                pathFinder.getShortestDistance(source, target)
        );
    }

    private PathResponse createResponse(List<Station> shortestPath, int shortestDistance) {
        return PathResponse.of(shortestPath, shortestDistance);
    }

    private Station findStationBy(Long id) {
        return stationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    private List<Section> findAllSection() {
        return lineRepository.findAll().stream()
                             .map(Line::findAllSections)
                             .flatMap(Collection::stream)
                             .collect(Collectors.toList());
    }



}
