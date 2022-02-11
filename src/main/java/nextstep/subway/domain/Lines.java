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
import java.util.List;
import java.util.stream.IntStream;

public class Lines {
    private List<Line> lines;
    private List<Section> sections;

    public Lines(List<Line> lines) {
        this.lines = lines;
        this.sections = parseSections(lines);
    }

    private List<Section> parseSections(List<Line> lines) {
        List<Section> sections = new ArrayList<>();
        lines.stream().forEach(line -> {
            line.getSections().stream()
                    .forEach(section -> {
                        sections.add(section);
                    });
        });
        return sections;
    }

    public List<Long> calculateShortestPath(Long source, Long target, List<StationResponse> allStations) {
        checkSourceEqualTarget(source, target);
        checkExistSourceAndTarget(source, target, allStations);
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertexToGraph(graph, allStations);
        addEdgeToGraph(graph, sections);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        checkConnectionBetweenSourceAndTarget(dijkstraShortestPath.getPath(source, target));

        List<Long> path = dijkstraShortestPath.getPath(source, target).getVertexList();

        return path;
    }

    private void checkExistSourceAndTarget(Long source, Long target, List<StationResponse> allStations) {
        if (!allStations.stream().anyMatch(stationResponse -> stationResponse.getId() == source)
                || !allStations.stream().anyMatch(stationResponse -> stationResponse.getId() == target)) {
            throw new NotExistedStationDeleteException();
        }
    }

    private void checkConnectionBetweenSourceAndTarget(GraphPath graphPath) {
        if (graphPath == null) {
            throw new UnConnectedSourceAndTargetException();
        }
    }

    private void checkSourceEqualTarget(Long source, Long target) {
        if (source == target) {
            throw new CantGetPathBySameStationException();
        }
    }

    private void addVertexToGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph,
            List<StationResponse> allStations) {
        allStations.stream()
                .forEach(stationResponse -> {
                    graph.addVertex(stationResponse.getId());
                });
    }

    private void addEdgeToGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Section> allSections) {
        allSections.stream().forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getDownStation().getId(), section.getUpStation().getId()),
                    section.getDistance());
        });
    }

    public int getShortestDistance(List<StationResponse> stationResponses) {
        return IntStream.rangeClosed(0, stationResponses.size() - 2)
                .map(i -> sections.stream()
                        .filter(section -> isMatchSection(stationResponses.get(i), stationResponses.get(i + 1),
                                section))
                        .map(section -> section.getDistance())
                        .findFirst()
                        .orElseThrow(() -> new UnConnectedSourceAndTargetException()))
                .reduce(Integer::sum).getAsInt();
    }

    private boolean isMatchSection(StationResponse stationA, StationResponse stationB, Section section) {
        return (section.getDownStation().getId() == stationA.getId() &&
                section.getUpStation().getId() == stationB.getId()) ||
                (section.getUpStation().getId() == stationA.getId() &&
                        section.getDownStation().getId() == stationB.getId());
    }
}