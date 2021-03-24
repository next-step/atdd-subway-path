package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    private List<Line> lines;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines){
        this.lines = lines;
    }

    public PathResponse getShortestPath(Station source, Station target){

        checkSourceNotEqualTarget(source, target);
        setGraph();
        PathResponse pathResponse = getPathResponse(source, target);

        return pathResponse;
    }

    // 최단경로 구하기
    private PathResponse getPathResponse(Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);

        if (graphPath == null) {
            new IllegalArgumentException("출발역과 도착역이 연결이 되어 있지 않습니다.");
        }

        List<Station> shortestPath = graphPath.getVertexList();
        List<StationResponse> paths = shortestPath.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        int distance = (int) graphPath.getWeight();

        PathResponse pathResponse = new PathResponse(paths, distance);
        return pathResponse;
    }

    // 그래프에 정점과 간선 세팅
    private void setGraph() {

        graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .map(line -> line.getSections().getSections())
                .flatMap(Collection::stream)
                .forEach(section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    int distance = section.getDistance();
                    // 정점 등록
                    graph.addVertex(upStation);
                    graph.addVertex(downStation);
                    // 간선 등록
                    graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
                });
    }

    private void checkSourceNotEqualTarget(Station source, Station target) {
        if(source.equals(target)){
            throw new IllegalArgumentException("출발역과 도착역은 같지 않아야 합니다.");
        }
    }

}
