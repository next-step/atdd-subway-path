package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Path {

    private final List<Station> stations;
    private final int distance;

    public static Path of(List<Station> shortestPath, int distance) {
        return new Path(shortestPath, distance);
    }
}
