package nextstep.subway.path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShortedPathFinderConfiguration {

    @Bean
    public ShortestPathFinder shortestPathFinder() {
        return new DijkstraShortestPathFinder();
    }
}
