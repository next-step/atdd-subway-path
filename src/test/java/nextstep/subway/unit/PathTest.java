package nextstep.subway.unit;

import nextstep.subway.domain.Path;
import nextstep.subway.utils.PathFixture;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.utils.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {

    @Test
    void findShortestPath() {
        // given
        PathFixture.라인을_구성한다();

        // when
        final Path path = Path.of(Arrays.asList(신분당선, 칠호선, 구호선), 고속터미널역, 신논현역);
        var shortestPath = path.getShortestPath();

        // then
        assertThat(shortestPath).containsSequence(고속터미널역, 반포역, 논현역, 신논현역);

        final int expectedDistance = 신분당선_논현역_신논현역.getDistance()
                + 칠호선_고속터미널역_반포역.getDistance()
                + 칠호선_반포역_논현역.getDistance();

        assertThat(path.getWeight()).isEqualTo(expectedDistance);
    }
}
