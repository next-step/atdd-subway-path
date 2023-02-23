package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.exception.StationsNotConnectedException;
import nextstep.subway.domain.sections.Sections;

public class SubwayMap {
    private static final String STATIONS_NOT_CONNECTED_EXCEPTION_MESSAGE = "두 역 간에 연결된 구간이 없습니다.";

    private final List<Station> stations;
    private final List<Section> sections;

    public SubwayMap(List<Station> stations, List<Section> sections) {
        this.stations = stations;
        this.sections = sections;
    }

    public SubwayPath findShortestPath(Station source, Station target) {
        WeightedMultigraph<Station, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);

        stations.forEach(graph::addVertex);
        sections.forEach(section -> {
            SectionEdge sectionEdge = new SectionEdge(section);
            graph.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
            graph.setEdgeWeight(sectionEdge, section.getDistance());
        });

        DijkstraShortestPath<Station, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, SectionEdge> shortestPath = dijkstraShortestPath.getPath(source, target);
        validateStationsAreConnected(shortestPath);

        return new SubwayPath(new Sections(
            shortestPath.getEdgeList().stream()
                .map(SectionEdge::getSection)
                .collect(Collectors.toList())
        ), source);
    }

    private void validateStationsAreConnected(GraphPath<Station, SectionEdge> shortestPath) {
        if (shortestPath == null) {
            throw new StationsNotConnectedException(STATIONS_NOT_CONNECTED_EXCEPTION_MESSAGE);
        }
    }

    private static class SectionEdge extends DefaultWeightedEdge {
        private final Section section;

        public SectionEdge(Section section) {
            this.section = section;
        }

        public Section getSection() {
            return section;
        }
    }
}
