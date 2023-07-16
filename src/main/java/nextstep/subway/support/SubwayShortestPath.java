package nextstep.subway.support;

import java.util.List;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;

public class SubwayShortestPath {
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;
    private final Station source;
    private final Station target;

    private SubwayShortestPath(final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath,
                               final Station source, final Station target) {
        this.dijkstraShortestPath = dijkstraShortestPath;
        this.source = source;
        this.target = target;
    }

    public List<Station> getStation() {
        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    public static Builder builder(final List<Station> stations, final List<Section> sections) {
        return new Builder(stations, sections);
    }

    public static class Builder {
        private final List<Station> stations;
        private final List<Section> sections;
        private Station source;
        private Station target;

        public Builder(final List<Station> stations, final List<Section> sections) {
            this.stations = stations;
            this.sections = sections;
        }

        public Builder source(final Station source) {
            this.source = source;
            return this;
        }

        public Builder target(final Station target) {
            this.target = target;
            return this;
        }

        public SubwayShortestPath build() {
            return new SubwayShortestPath(makeGraph(stations, sections), source, target);
        }

        private DijkstraShortestPath<Station, DefaultWeightedEdge> makeGraph(final List<Station> stations,
                                                                             final List<Section> sections) {
            final var graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);

            registerVertexes(graph, stations);
            registerEdges(graph, sections);

            return new DijkstraShortestPath<>(graph);
        }

        private void registerVertexes(final WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                                      final List<Station> stations) {
            for (Station station : stations) {
                graph.addVertex(station);
            }
        }

        private void registerEdges(final WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                                   final List<Section> sections) {
            for (final var section : sections) {
                final var upStation = section.getUpStation();
                final var downStation = section.getDownStation();
                graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
            }
        }
    }
}
