package nextstep.subway.path.config;

import nextstep.subway.path.service.DijkstraShortestPathService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class finderConfig {

    @Value("${finder}")
    private String finderImplementation;

    @Bean
    public DijkstraShortestPathService dijkstraShortestPathService() {
        if ("dijkstra".equalsIgnoreCase(finderImplementation)) {
            return new DijkstraShortestPathService();
        }

        throw new IllegalArgumentException("Unsupported finder");
    }
}
