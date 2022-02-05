package nextstep.subway.domain.path;

import static nextstep.subway.exception.CommonExceptionMessages.NULL_SHORTEST_PATH;

import java.util.List;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidPathSearchingException;
import org.jgrapht.GraphPath;

public class ShortestPath {

    private GraphPath path;

    public ShortestPath(GraphPath path) {
        validate(path);

        this.path = path;
    }

    private void validate(GraphPath path) {
        if (path == null) {
            throw new InvalidPathSearchingException(NULL_SHORTEST_PATH);
        }
    }

    public List<Station> stations() {
        return path.getVertexList();
    }

    public int totalDistance() {
        return (int) path.getWeight();
    }

    public ShortestPath get() {
        return this;
    }
}
