package nextstep.subway.path.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {
    @DisplayName("getPathFinder의 입력이 PathType.DISTANCE 이면 ShortestPathFinder 리턴")
    @Test
    void getPathFinderWithDistanceReturnsShortestPathFinder() {
        // when
        PathFinder pathFinder = PathFinder.getPathFinder(PathType.DISTANCE);

        // then
        assertThat(pathFinder).isInstanceOf(ShortestPathFinder.class);
    }

    @DisplayName("getPathFinder의 입력이 PathType.DURATION 이면 FastestPathFinder 리턴")
    @Test
    void getPathFinderWithDurationReturnsFastestPathFinder() {
        // when
        PathFinder pathFinder = PathFinder.getPathFinder(PathType.DURATION);

        // then
        assertThat(pathFinder).isInstanceOf(FastestPathFinder.class);
    }
}