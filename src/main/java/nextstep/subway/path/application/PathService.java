package nextstep.subway.path.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.SectionService;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.path.domain.Edge;
import nextstep.subway.path.domain.Graph;
import nextstep.subway.path.domain.Path;
import nextstep.subway.station.applicaion.StationService;
import nextstep.subway.station.applicaion.dto.response.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;

    public PathResponse findPath(Long source, Long target) {
        List<StationResponse> stations = stationService.findAllStations();

        List<Long> stationIds = stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        if (!stationIds.contains(source) || !stationIds.contains(target)) {
            throw new IllegalArgumentException();
        }

        List<Edge> sections = sectionService.findAllSections()
                .stream()
                .map(it -> new Edge(it.getUpStationId(), it.getDownStationId(), it.getDistance()))
                .collect(Collectors.toList());

        Path shortestPath = new Graph(stationIds, sections).findShortestPath(source, target);
        List<Long> paths = shortestPath.getVertexes();

        List<StationResponse> pathStations = stations.stream()
                .filter(s -> paths.contains(s.getId()))
                .sorted(comparing(s -> paths.indexOf(s.getId())))
                .collect(Collectors.toList());

        return new PathResponse(pathStations, shortestPath.getDistance());
    }
}
