package subway.section;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.station.Station;

import java.util.List;
import java.util.stream.Collectors;

public class Path {
    private final List<Section> sections;

    public Path(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getStations(Station start, Station end) {
        // graph 설정
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Station station : toStations()) {
            if (!graph.containsVertex(station)) {
                graph.addVertex(station);
            }
        }
        for (Section section : sections) {
            if (!graph.containsEdge(section.getUpStation(), section.getDownStation())) {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }
        // 다익스트라 알고리즘을 통한 최단거리 경로 계산
        DijkstraShortestPath<Station, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(start, end);
        return path.getVertexList();
    }

    private List<Station> toStations() {
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(getFirstStation());
        return stations;
    }

    private Station getFirstStation() {
        return sections.stream()
                .filter(section -> sections.stream().noneMatch(
                        other -> section.equalUpStation(other.getDownStation())
                )).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선에 구간이 존재하지 않습니다."))
                .getUpStation();
    }
}
