package nextstep.subway.graph;

import nextstep.subway.linesection.LineSectionRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class GraphService {
    private final StationRepository stationRepository;
    private final LineSectionRepository lineSectionRepository;

    public GraphService(StationRepository stationRepository, LineSectionRepository lineSectionRepository) {
        this.stationRepository = stationRepository;
        this.lineSectionRepository = lineSectionRepository;
    }

    public WeightedMultigraph<Station, DefaultWeightedEdge> getGraph() {
        var graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        stationRepository.findAll()
                .forEach(e -> graph.addVertex(e));
        lineSectionRepository.findAll()
                .forEach(e -> graph.setEdgeWeight(graph.addEdge(e.getUpStation(), e.getDownStation()), e.getDistance()));
        return graph;
    }
}
