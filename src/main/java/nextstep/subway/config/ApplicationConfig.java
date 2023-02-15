package nextstep.subway.config;

import nextstep.subway.domain.DijkstraPathFinder;
import nextstep.subway.domain.PathFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public PathFinder pathFinder() {
        return new DijkstraPathFinder();
    }
}
