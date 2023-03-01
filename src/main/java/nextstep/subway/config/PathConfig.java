package nextstep.subway.config;

import nextstep.subway.applicaion.DijkstraPathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {
    @Bean
    public DijkstraPathFinder dijkstraPathFinder() {
        WeightedMultigraph<Station, DefaultWeightedEdge> weightedMultigraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        return new DijkstraPathFinder(weightedMultigraph);
    }
}
