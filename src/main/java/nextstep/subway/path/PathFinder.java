package nextstep.subway.path;

import nextstep.subway.section.Section;
import org.jgrapht.alg.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    public void apply(boolean isAdd, Section section) {

    }

    public Pair<List<String>, Integer> findShortestPath(String sourceId, String targetId) {
        List<String> shortestPath = null;
        int totalDistance = 0;
        return Pair.of(shortestPath, totalDistance);
    }
}
