package nextstep.subway.util;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;


public class PathFinder {
    private DijkstraShortestPath dijkstraShortestPath;

    public void init(List<Line> lineList) {
        dijkstraShortestPath = new DijkstraShortestPath(buildGraph(lineList));
    }

    public List<Station> getPath(Station source, Station target) {

        validateGraph(source, target, dijkstraShortestPath);

        try{
            return dijkstraShortestPath.getPath(source, target).getVertexList();
        }
        catch (Exception e){
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않음.");
        }



    }

    public int getDistance(Station source, Station target) {
        validateGraph(source, target, dijkstraShortestPath);
        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph(List<Line> lineList) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        addVertex(lineList, graph);
        addEdge(lineList, graph);


        return graph;
    }

    private void addEdge(List<Line> lineList, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lineList.stream()
                .flatMap(line -> line.getSections().getSectionList().stream())
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation())
                        , section.getDistance()));
    }

    private void addVertex(List<Line> lineList, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lineList.stream()
                .flatMap(line -> line.getOrderedStationList().stream())
                .forEach(station -> graph.addVertex(station));
    }

    private void validateGraph(Station source, Station target, DijkstraShortestPath dijkstraShortestPath) {

        if(source.getId()==target.getId()){
            throw new IllegalArgumentException("경로조회는 출발역과 도착역이 동일할 수 없음.");
        }

    }
}