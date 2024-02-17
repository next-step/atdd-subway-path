package nextstep.subway.service;

import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.domain.response.StationResponse;
import nextstep.subway.repository.SectionRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public PathService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);

        // 그래프에 모든 역 추가
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        createSubwayGraph(graph);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(source, target);

        // 최단 경로 리턴
        return createPathResponse(path);
    }

    private PathResponse createPathResponse(GraphPath path) {
        List<Station> stations = path.getVertexList();
        int shortestDistance = (int) path.getWeight();

        return new PathResponse(createStationResponseList(stations), shortestDistance);
    }

    private List<StationResponse> createStationResponseList(List<Station> stations) {
        List<StationResponse> stationResponseList = new ArrayList<>();
        for (Station station : stations) {
            stationResponseList.add(stationService.createStationResponse(station));
        }
        return stationResponseList;
    }

    private void createSubwayGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        List<Section> sections = sectionRepository.findAll();
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation= section.getDownStation();

            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
    }
}
