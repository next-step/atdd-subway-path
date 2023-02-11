package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lines {

    private final Set<Line> values;

    public static Lines from(Set<Line> lineSet) {
        return new Lines(lineSet);
    }

    public PathFinder toPathFinder() {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return values.size();
    }
}
