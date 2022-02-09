package nextstep.subway.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShortestPathFindConfig {

    @Bean
    public ShortestPathFindAlgorithm<Station, Line, Integer> shortestPathFindAlgorithm() {
        return new LinesJGraphDijkstraAlgorithm();
    }
}
