package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SameOriginPathException;
import nextstep.subway.exception.StationNotFoundException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PathFinder {

    private final SectionRepository sectionRepository;

    public PathFinder(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public PathResponse find(Long sourceId, Long targetId) {
        validate(sourceId, targetId);
        List<Section> sections = sectionRepository.findAll();
        Set<Station> stations = getStationSet(sections);
        Station sourceStation = findStation(sourceId, stations);
        Station targetStation = findStation(targetId, stations);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createWeightedMultigraph(sections, stations);

        List<Station> shortestPath = getShortestPath(graph, sourceStation, targetStation);
        double weight = getWeight(graph, sourceStation, targetStation);

        return new PathResponse(shortestPath, (int) weight);
    }

    private void validate(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            throw new SameOriginPathException();
        }
    }

    private double getWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                             Station sourceStation,
                             Station targetStation) {

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
    }

    private List<Station> getShortestPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
                                          Station sourceStation,
                                          Station targetStation) {

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createWeightedMultigraph(List<Section> sections,
                                                                                      Set<Station> stations) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section ->
                graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()
                )
        );
        return graph;
    }

    private Set<Station> getStationSet(List<Section> sections) {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toSet());
    }

    private Station findStation(Long sourceId, Set<Station> stations) {
        return stations.stream().filter(station -> station.hasId(sourceId)).findAny()
                .orElseThrow(StationNotFoundException::new);
    }
}
