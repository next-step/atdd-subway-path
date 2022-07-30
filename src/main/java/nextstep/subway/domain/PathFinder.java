package nextstep.subway.domain;

import nextstep.subway.exception.BusinessException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath dijkstraShortestPath;


    public PathFinder(List<Section> sectionList) {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertex(getStationList(sectionList));
        addEdgeWeight(sectionList);
        this.dijkstraShortestPath = new DijkstraShortestPath(this.graph);
    }

    private void addEdgeWeight(List<Section> sectionList) {
        sectionList.forEach(section -> this.graph.setEdgeWeight(this.graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void addVertex(List<Station> sectionList) {
        sectionList.forEach(section -> this.graph.addVertex(section));
    }

    public List<Station> getShortestPath(Station sourceStation, Station targetStation) {
        GraphPath graphPath = this.dijkstraShortestPath.getPath(sourceStation, targetStation);

        if (Objects.isNull(graphPath)) {
            throw new BusinessException("sourceStation과 targetStation을 연결해주는 경로가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return graphPath.getVertexList();
    }

    public long getShortestDistance(Station sourceStation, Station targetStation) {
        return (long) this.dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
    }

    private List<Station> getStationList(List<Section> sectionList) {
        return sectionList.stream()
                .flatMap(section -> List.of(section.getUpStation(), section.getDownStation())
                        .stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
