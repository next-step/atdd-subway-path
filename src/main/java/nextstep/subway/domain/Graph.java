package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.exception.CantGetPathBySameStationException;
import nextstep.subway.exception.NotExistedStationDeleteException;
import nextstep.subway.exception.UnConnectedSourceAndTargetException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Graph {
    private List<Section> allSections;
    private List<Station> allStations;
    private WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public Graph(List<Line> allLines) {
        this.graph = createGraph(parseAllSections(allLines), parseAllStations(allLines));
        this.allSections = parseAllSections(allLines);
        this.allStations = parseAllStations(allLines);
    }

    private List<Section> parseAllSections(List<Line> allLines) {
        List<Section> allSections = new ArrayList<>();
        allLines.stream().forEach(line -> {
            allSections.addAll(line.getSections());
        });
        return allSections;
    }

    private List<Station> parseAllStations(List<Line> allLines) {
        Set<Station> allStationsSet = new HashSet<Station>();

        parseAllSections(allLines).stream().forEach(section -> {
            allStationsSet.add(section.getDownStation());
            allStationsSet.add(section.getUpStation());
        });

        List<Station> allStations = new ArrayList<>(allStationsSet);
        return allStations;
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> createGraph(List<Section> allSections, List<Station> allStations) {
        addVertexToGraph(allStations);
        addEdgeToGraph(allSections);
        return graph;
    }

    private void addVertexToGraph(List<Station> allStations) {
        allStations.stream()
                .forEach(station -> {
                    graph.addVertex(station.getId());
                });
    }

    private void addEdgeToGraph(List<Section> allSections) {
        allSections.stream().forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getDownStation().getId(), section.getUpStation().getId()),
                    section.getDistance());
        });
    }

    public List<Long> dijkstraShortestPath(Long source, Long target) {
        checkSourceEqualTarget(source, target);
        checkExistSourceAndTarget(source, target, allStations);
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        List<Long> path = dijkstraShortestPath.getPath(source, target).getVertexList();
        checkConnectionBetweenSourceAndTarget(path);

        return path;
    }

    public int dijkstraShortestDistance(List<Long> path) {
        return IntStream.rangeClosed(0, path.size() - 2)
                .map(i -> allSections.stream()
                        .filter(section -> isMatchSection(path.get(i), path.get(i + 1),
                                section))
                        .map(section -> section.getDistance())
                        .findFirst()
                        .orElseThrow(() -> new UnConnectedSourceAndTargetException()))
                .reduce(Integer::sum).getAsInt();
    }

    private boolean isMatchSection(Long sourceId, Long targetId, Section section) {
        return (section.getDownStation().getId() == sourceId &&
                section.getUpStation().getId() == targetId) ||
                (section.getUpStation().getId() == sourceId &&
                        section.getDownStation().getId() == targetId);
    }

    public List<Section> getAllSections() {
        return allSections;
    }

    public List<Station> getAllStations() {
        return allStations;
    }

    private void checkExistSourceAndTarget(Long source, Long target, List<Station> allStations) {
        if (!allStations.stream().anyMatch(stationResponse -> stationResponse.getId() == source)
                || !allStations.stream().anyMatch(stationResponse -> stationResponse.getId() == target)) {
            throw new NotExistedStationDeleteException();
        }
    }

    private void checkConnectionBetweenSourceAndTarget(List<Long> graphPath) {
        if (graphPath == null) {
            throw new UnConnectedSourceAndTargetException();
        }
    }

    private void checkSourceEqualTarget(Long source, Long target) {
        if (source == target) {
            throw new CantGetPathBySameStationException();
        }
    }
}
