package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.SourceAndTargetSameException;
import nextstep.subway.exception.StationNotExistException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        if (Objects.equals(source, target)) {
            throw new SourceAndTargetSameException("구간 조회 시 출발역과 도착역이 같을 수 없습니다.");
        }
        List<Line> lines = lineRepository.findAll();
        List<Section> sections = allSectionsFrom(lines);
        GraphPath<Station, DefaultWeightedEdge> path = shortestPath(source, target, sections);
        List<Station> stations = path.getVertexList();
        int distance = (int) path.getWeight();
        return new PathResponse(toStationResponses(stations), distance);
    }

    private List<Section> allSectionsFrom(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private GraphPath<Station, DefaultWeightedEdge> shortestPath(Long source, Long target, List<Section> sections) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath<>(toWeightedMultiGraph(sections));
        return dijkstraShortestPath.getPath(stationFrom(source), stationFrom(target));
    }

    private Station stationFrom(Long source) {
        return stationRepository.findById(source).orElseThrow(() -> new StationNotExistException("id에 해당하는 역이 존재하지 않습니다."));
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> toWeightedMultiGraph(List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sections.forEach(section -> setupVertexAndEdge(graph, section));
        return graph;
    }

    private void setupVertexAndEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
    }

    private List<StationResponse> toStationResponses(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList());
    }
}
