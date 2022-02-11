package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.StationResponse;
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
        WeightedMultigraph<Long, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertexToGraph(graph, allStations);
        addEdgeToGraph(graph, sections);

        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(graph);

        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    private void addVertexToGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<StationResponse> allStations) {
        allStations.stream()
                .forEach(stationResponse -> {
                    graph.addVertex(stationResponse.getId());
                });
    }

    private void addEdgeToGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Section> allSections) {
        allSections.stream().forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getDownStation().getId(), section.getUpStation().getId()), section.getDistance());
        });
    }

    public int getShortestDistance(List<StationResponse> stationResponses) {
        return IntStream.rangeClosed(0, stationResponses.size() - 2)
                .map(i -> sections.stream()
                        .filter(section -> section.getDownStation().getId() == stationResponses.get(i).getId() &&
                                section.getUpStation().getId() == stationResponses.get(i + 1).getId() ||
                                section.getUpStation().getId() == stationResponses.get(i).getId() &&
                                        section.getDownStation().getId() == stationResponses.get(i + 1).getId())
                        .map(section -> section.getDistance()).findFirst().get()).reduce(Integer::sum).getAsInt();
    }
}
