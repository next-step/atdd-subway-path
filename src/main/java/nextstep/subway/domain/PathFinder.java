package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author a1101466 on 2022/07/27
 * @project subway
 * @description
 */
public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

    private List<Line> lines = new ArrayList<>();

    public PathFinder(List<Line> lines) {
        this.lines = lines;
        addStationToVertex(lines);
        addSectionEdgeWeight(lines);
    }

    public PathFinder() {
    }

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station sourceStation, Station targetStation){
        isRequestValid(sourceStation, targetStation);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        isConnectCheck(shortestPath);
        return shortestPath;
    }

    private void isRequestValid(Station sourceStation, Station targetStation){
        if(Objects.equals(sourceStation, targetStation)){
            throw new BadRequestException("출발역과 도착역은 같을 수 없습니다.");
        }
        if(checkNotExistStation(sourceStation)){
            throw new BadRequestException("존재하지 않는 출발역 입니다.");
        }
        if(checkNotExistStation(targetStation)){
            throw new BadRequestException("존재하지 않는 도착역 입니다.");
        }
    }

    private void isConnectCheck(GraphPath<Station, DefaultWeightedEdge> shortestPath){
        if(Objects.equals(null, shortestPath)){
            throw new BadRequestException("출발역과 도착역이 연결이 되어 있지 않은 경우");
        }
    }

    private void addStationToVertex(List<Line> lines){
        lines.stream()
                .map(Line::getStations)
                .flatMap(List::stream)
                .forEach(graph::addVertex);
    }

    private void addSectionEdgeWeight(List<Line> lines){
        lines.stream()
                .map(Line::getSections)
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .forEach(section ->
                        graph.setEdgeWeight(
                                graph.addEdge(section.getUpStation()
                                        , section.getDownStation())
                                ,section.getDistance()
                        )
                );
    }
    private Boolean checkNotExistStation(Station station){
        return lines.stream()
                .map(Line::getStations)
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .stream()
                .noneMatch(currentStation -> currentStation.equals(station));
    }

}
