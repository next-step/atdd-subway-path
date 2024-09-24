package nextstep.subway.path.api;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import nextstep.subway.path.api.response.PathResponse;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class PathService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public PathResponse getPath(Long source, Long target) {
        validateStation(source, target);
        return findPath(source, target);
    }

    private void validateStation(Long source, Long target) {
        stationRepository.findById(source).orElseThrow(
                () -> new EntityNotFoundException("출발역을 찾을 수 없습니다.")
        );

        stationRepository.findById(target).orElseThrow(
                () -> new EntityNotFoundException("종착역을 찾을 수 없습니다.")
        );

        if (source == target) {
            throw new IllegalArgumentException("출발역과 종착역이 같습니다.");
        }
    }

    public PathResponse findPath(Long sourceId, Long targetId) {
        List<Section> sections = sectionRepository.findAll();
        SubwayGraph subwayGraph = new SubwayGraph(sections);
        DijkstraShortestPath dijkstraShortestPath = subwayGraph.getShortestPath();
        GraphPath shortestPath = null;

        try {
            shortestPath = dijkstraShortestPath.getPath(sourceId, targetId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("출발역과 종착역이 연결되어 있지 않습니다.");
        }

        if (shortestPath == null) {
            throw new IllegalArgumentException("출발역과 종착역이 연결되어 있지 않습니다.");
        }

        if (shortestPath.getWeight() == 0) {
            throw new IllegalArgumentException("출발역과 종착역이 같습니다.");
        }

        return PathResponse.of(convertToStationResponses(shortestPath.getVertexList()), (int) shortestPath.getWeight());
    }

    private List<StationResponse> convertToStationResponses(List<Long> stationIds) {
        Map<Long, Station> stations = stationRepository.findAll().stream()
                .collect(Collectors.toMap(Station::getId, station -> station));

        return stationIds.stream()
                .map(id -> {
                    Station station = stations.get(id);
                    return station != null ? StationResponse.of(station) : null;
                })
                .filter(Objects::nonNull) // null 필터링
                .collect(Collectors.toList());
    }
}
