package nextstep.subway.domain;

import java.util.Collections;
import java.util.List;

public class Path {
    private List<Long> routes;
    private int distance;

    private Path() {
    }

    public Path(List<Long> routes, int distance) {
        this.routes = routes;
        this.distance = distance;
    }

    public List<Long> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    public int getDistance() {
        return distance;
    }
}
