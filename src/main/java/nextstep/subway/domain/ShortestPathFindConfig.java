package nextstep.subway.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ShortestPathFindConfig {

    @Bean
    @Scope("prototype")
    public ShortestPathFinder<Station, Line, Integer> shortestPathFindAlgorithm() {
        return new LinesJGraphShortestPathFinder();
    }
}
