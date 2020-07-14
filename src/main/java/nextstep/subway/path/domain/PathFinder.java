package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PathFinder {

    public void findShortestDistance(Station startStation, Station endStation) {

    }

    @Bean
    public PathFinder pathFinder() {
        return new PathFinder();
    }
}
