package nextstep.subway.unit;

import nextstep.subway.domain.Path;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.SectionFixture.구간생성;
import static nextstep.subway.utils.StationFixture.역생성;
import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {

    @Test
    void findShortestPath() {
        // given
        var 칠호선 = 라인_생성_7호선();
        var 구호선 = 라인_생성_9호선();
        var 신분당선 = 라인_생성_신분당선();

        var 고속터미널역 = 역생성("고속터미널역");
        var 반포역 = 역생성("반포역");
        var 논현역 = 역생성("논현역");
        var 사평역 = 역생성("사평역");
        var 신논현역 = 역생성("신논현역");

        var 칠호선_고속터미널역_반포역 = 구간생성(칠호선, 고속터미널역, 반포역, 2);
        var 칠호선_반포역_논현역 = 구간생성(칠호선, 반포역, 논현역, 2);
        var 신분당선_논현역_신논현역 = 구간생성(신분당선, 논현역, 신논현역, 3);
        var 구호선_고속터미널역_신논현 = 구간생성(구호선, 고속터미널역, 신논현역, 10);

        칠호선.addSection(칠호선_고속터미널역_반포역);
        칠호선.addSection(칠호선_반포역_논현역);
        신분당선.addSection(신분당선_논현역_신논현역);
        구호선.addSection(구호선_고속터미널역_신논현);

        // when
        final Path path = Path.of(Arrays.asList(신분당선, 칠호선, 구호선));
        var shortestPath = path.getShortestPath(고속터미널역, 신논현역);

        // then
        assertThat(shortestPath.getVertexList()).containsSequence(고속터미널역, 반포역, 논현역, 신논현역);

        final double expectedDistance = 신분당선_논현역_신논현역.getDistance()
                + 칠호선_고속터미널역_반포역.getDistance()
                + 칠호선_반포역_논현역.getDistance();
        assertThat(shortestPath.getWeight()).isEqualTo(expectedDistance);
    }
}
