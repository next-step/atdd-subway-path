package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import nextstep.subway.exception.PathNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PathFinder {
    private final LineRepository lineRepository;

    private WeightedMultigraph<String, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

    @PostConstruct
    public void init() {
        lineRepository.findAll().forEach(line -> {
            line.getSections().forEach(section -> {
                String upStationName = Long.toString(section.getUpstation().getId());
                String downStationName = Long.toString(section.getDownstation().getId());

                graph.addVertex(upStationName);
                graph.addVertex(downStationName);

                DefaultWeightedEdge edge = graph.addEdge(upStationName, downStationName);

                if (edge != null) {
                    graph.setEdgeWeight(edge, section.getDistance());
                }

            });
        });
    }

    public synchronized void addPath(boolean isMiddle, Section addedSection, Sections sections) {
        String upstationId = Long.toString(addedSection.getUpstation().getId());
        String downstationId = Long.toString(addedSection.getDownstation().getId());

        if (isMiddle) {
            String firstStationId = upstationId;
            String secondStationId = downstationId;

            Section nextSection = sections.getNextSection(addedSection);
            String thirdStationId = Long.toString(nextSection.getDownstation().getId());

            DefaultWeightedEdge edgeToRemove = graph.getEdge(firstStationId, thirdStationId);
            if (edgeToRemove != null) {
                graph.removeEdge(edgeToRemove);
            }

            // 그리고 first와 second, second와 third 연결하고 싶음
            graph.addVertex(secondStationId);

            DefaultWeightedEdge edgeToAdd = graph.addEdge(firstStationId, secondStationId);
            if (edgeToAdd != null) {
                graph.setEdgeWeight(edgeToAdd, addedSection.getDistance());
            }

            edgeToAdd = graph.addEdge(secondStationId, thirdStationId);
            if (edgeToAdd != null) {
                graph.setEdgeWeight(edgeToAdd, nextSection.getDistance());
            }
            return;
        }

        graph.addVertex(upstationId);
        graph.addVertex(downstationId);

        DefaultWeightedEdge edge = graph.addEdge(upstationId, downstationId);

        if (edge != null) {
            graph.setEdgeWeight(edge, addedSection.getDistance());
        }
    }

    public synchronized void removePath(boolean isMiddle, Station removedStation, Section removedSection, Sections sections) {
        String removedStationId = Long.toString(removedStation.getId());

        graph.removeVertex(removedStationId);

        if (isMiddle) {
            Station remainingStation = removedSection.getDownstation();
            String remainingStationId = Long.toString(remainingStation.getId());
            DefaultWeightedEdge edgeToRemove = graph.getEdge(remainingStationId, removedStationId);
            if (edgeToRemove != null) {
                graph.removeEdge(edgeToRemove);
            }

            Iterator<Section> sectionIterator = sections.iterator();
            while (sectionIterator.hasNext()) {
                Section section = sectionIterator.next();
                if (section.isDownstation(remainingStation)) {
                    String oppositeStationId = Long.toString(section.getUpstation().getId());

                    DefaultWeightedEdge newEdge = graph.addEdge(remainingStationId, oppositeStationId);
                    if (newEdge != null) {
                        graph.setEdgeWeight(newEdge, section.getDistance());
                    }
                }
            }
            return;
        }

        Station remainingStation = (removedSection.isUpstation(removedStation) ? removedSection.getDownstation() : removedSection.getUpstation());
        String remainingStationId = Long.toString(remainingStation.getId());
        DefaultWeightedEdge edgeToRemove = graph.getEdge(remainingStationId, removedStationId);
        if (edgeToRemove != null) {
            graph.removeEdge(edgeToRemove);
        }

    }

    public Pair<List<String>, Integer> findShortestPath(String sourceId, String targetId) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceId, targetId);

        if (path == null) {
            throw new PathNotFoundException("가능한 경로가 존재하지 않습니다.");
        }

        List<String> shortestPath = path.getVertexList();
        int totalDistance = (int) path.getEdgeList().stream()
                .mapToDouble(edge -> graph.getEdgeWeight(edge))
                .sum();

        return Pair.of(shortestPath, totalDistance);
    }
}
