package nextstep.subway.path.application;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import nextstep.subway.station.domain.Station;

@Component
public class ShortestPathFinder {

    @Bean
    public ShortestPathFinder shortestPathFinder() {
        return new ShortestPathFinder();
    }

    public void findShortestDistance(Station startStation, Station endStation) {
        return;
    }
}
