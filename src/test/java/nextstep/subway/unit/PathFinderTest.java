package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("최단 경로 조회")
public class PathFinderTest {

    /**
     * 강남 -> 천호 = 10
     * 강남 -> 군자 -> 천호 = 8 (최단거리)
     */
    @DisplayName("lines를 인자로 받아 최단 경로 역 목록과 거리의 합을 반환한다")
    @Test
    void shortestPath() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 군자역 = new Station(2L, "군자역");
        Station 천호역 = new Station(3L, "천호역");
        Line 신분당선 = new Line("신분당선", "빨간색");
        신분당선.addSection(Section.of(신분당선, 강남역, 군자역, 5));
        Line 구분당선 = new Line("구분당선", "파란색");
        구분당선.addSection(Section.of(구분당선, 강남역, 천호역, 10));
        Line 오호선 = new Line("오호선", "보라색");
        오호선.addSection(Section.of(오호선, 군자역, 천호역, 3));
        List<Line> lines = Arrays.asList(신분당선, 구분당선, 오호선);

        // when
        Path path = PathFinder.findShortestPath(lines, 강남역, 천호역);

        // then
        assertThat(path.getStations()).containsExactly(강남역, 군자역, 천호역);
        assertThat(path.getDistance()).isEqualTo(8);
    }
}
