package nextstep.subway.path.domain;

import nextstep.subway.path.exception.InvalidSourceTargetException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PathFinderTest {

    @DisplayName("출발역과 도착역이 같은 경우 InvalidSourceTargetException 이 발생합니다.")
    @Test
    void invalidSourceTarget() {
        // given
        PathFinder pathFinder = new PathFinder(emptyList());
        Station 강남역 = new Station("강남역");


        // when, then
        assertThatExceptionOfType(InvalidSourceTargetException.class)
                .isThrownBy(() -> pathFinder.findShortestPath(강남역, 강남역));
    }
}
