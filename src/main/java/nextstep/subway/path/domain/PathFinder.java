package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.path.exception.SameSourceTargetException;
import nextstep.subway.path.exception.SourceTargetNotReachable;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder {
    
    private List<Station> stations;
    
    private List<Line> lines;

    private Map<Long, Station> stationMap = new HashMap<>();

    private WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);


    public PathFinder(List<Station> stations, List<Line> lines) {
        this.stations = stations;
        this.lines = lines;
        for(Station station : stations){
            stationMap.put(station.getId(), station);
        }
        setUpGraph();
    }

    private void setUpGraph() {
        addVertex(graph);
        setEdgeWeight(graph);
    }

    public List<Station> getShortestPathList(long source, long target) throws NullPointerException {
        GraphPath<Long, DefaultWeightedEdge> graphPath = getGraphPath(source, target);
        return graphPath.getVertexList()
                .stream()
                .map(id -> extractStation(id))
                .collect(Collectors.toList());
    }

    private Station extractStation(Long id){
        return stationMap.get(id);
    }


    public int getShortestPathLength(long source, long target) throws NullPointerException {
        validateStationExist(source, target);
        GraphPath<Long, DefaultWeightedEdge> graphPath = getGraphPath(source, target);
        return (int) graphPath.getWeight();
    }

    public GraphPath<Long, DefaultWeightedEdge> getGraphPath(long source, long target){
        validateSourceTargetSame(source, target);
        GraphPath<Long, DefaultWeightedEdge> graphPath = new DijkstraShortestPath(graph).getPath(source, target);
        validateSourceTargetReachable(graphPath);
        return graphPath;
    }

    public void addVertex(WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        for (Station station : stations) {
            graph.addVertex(station.getId());
        }
    }

    public void setEdgeWeight(WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        for (Line line : lines) {
            setEdgeWeight(graph, line);
        }
    }

    private void setEdgeWeight(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Line line) {
        for (Section section : line.getSections().getSectionList()) {
            graph.setEdgeWeight(
                    graph.addEdge(
                            section.getUpStation().getId(),
                            section.getDownStation().getId()
                    )
                    , section.getDistance()
            );
        }
    }

    private void validateSourceTargetSame(long source, long target) {
        if (source == target) {
            throw new SameSourceTargetException("출발역과 도착역이 같습니다");
        }
    }

    private void validateSourceTargetReachable(GraphPath<Long, DefaultWeightedEdge> graphPath) {
        if (Objects.isNull(graphPath)) {
            throw new SourceTargetNotReachable("출발역과 도착역이 만날 수 없습니다");
        }
    }

    private void validateStationExist(long source, long target){
        if(!stationMap.containsKey(source) || !stationMap.containsKey(target)){
            throw new NoSuchElementException("출발역 또는 도착역이 없습니다");
        }
    }
}
