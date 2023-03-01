package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PathService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    public PathService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public PathResponse searchPath(Long source, Long target) {
        if(source == target){
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        DijkstraShortestPath map = getSubwayMap();


            int distance = calculateDistance(map, sourceStation.getId(), targetStation.getId());
            List<StationResponse> path = findPath(map, sourceStation.getId(), targetStation.getId());



        return new PathResponse(distance, path);
    }

    private DijkstraShortestPath getSubwayMap(){
        List<Line> lines = lineRepository.findAll();
        WeightedMultigraph<Long, DefaultWeightedEdge> graph =
                new WeightedMultigraph(DefaultWeightedEdge.class);


        addStationToMap(graph, lines);
        addSectionToMap(graph, lines);

        DijkstraShortestPath map = new DijkstraShortestPath(graph);

        return map;
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> addStationToMap(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Line> lines){
        for(Line line : lines){
            for(Station station : line.getSortedStations()){
                graph.addVertex(station.getId());
            }
        }

        return graph;
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> addSectionToMap(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Line> lines){
        for(Line line : lines){
            for(Section section : line.getSections()){
                graph.setEdgeWeight(graph.addEdge(section.getUpStation().getId(),section.getDownStation().getId()), section.getDistance());
            }
        }

        return graph;
    }

    private List<StationResponse> findPath(DijkstraShortestPath map, Long source, Long target){
        List<StationResponse> pathWithStationResponse = new ArrayList<>();
        List<Long> pathWithStationId = map.getPath(source, target).getVertexList();

        for(Long id : pathWithStationId){
            pathWithStationResponse.add(stationService.findStation(id));
        }

        return pathWithStationResponse;
    }

    private int calculateDistance(DijkstraShortestPath map, Long source, Long target){
        int distance = 0;

        try {
            distance = (int) map.getPathWeight(source, target);
        }catch(Exception e) {
            throw new IllegalArgumentException("연결되지 않은 역입니다.");
        }

        return distance;
    }
}
