package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.FindPathFailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("최단 경로 조회")
public class PathFinderTest {

    private Station 강남역;
    private Station 군자역;
    private Station 천호역;
    private Line 신분당선;
    private Line 구분당선;
    private Line 오호선;
    private List<Line> lines;
    private int 강남_군자 = 5;
    private int 강남_천호 = 10;
    private int 군자_천호 = 3;

    /**
     * 강남 -> 천호 = 10
     * 강남 -> 군자 -> 천호 = 8 (최단거리)
     */
    @BeforeEach
    void setup() {
        // given
        강남역 = new Station(1L, "강남역");
        군자역 = new Station(2L, "군자역");
        천호역 = new Station(3L, "천호역");

        신분당선 = new Line("신분당선", "빨간색");
        신분당선.addSection(Section.of(신분당선, 강남역, 군자역, 강남_군자));
        구분당선 = new Line("구분당선", "파란색");
        구분당선.addSection(Section.of(구분당선, 강남역, 천호역, 강남_천호));
        오호선 = new Line("오호선", "보라색");
        오호선.addSection(Section.of(오호선, 군자역, 천호역, 군자_천호));

        lines = Arrays.asList(신분당선, 구분당선, 오호선);
    }

    @DisplayName("lines를 인자로 받아 최단 경로 역 목록과 거리의 합을 반환한다")
    @Test
    void shortestPath() {
        // given
        PathFinder pathFinder = new PathFinder(lines);

        // when
        Path path = pathFinder.findShortestPath(강남역, 천호역);

        // then
        assertThat(path.getStations()).containsExactly(강남역, 군자역, 천호역);
        assertThat(path.getDistance()).isEqualTo(8);
    }

    @DisplayName("같은 출발역 도착역간의 경로 조회시 에러가 발생한다")
    @Test
    void sameStationsThrowException() {
        // given
        PathFinder pathFinder = new PathFinder(lines);

        // when & then
        assertThatThrownBy(() -> pathFinder.findShortestPath(강남역, 강남역))
                .isInstanceOf(FindPathFailException.class)
                .hasMessage("같은 출발역과 도착역 경로 조회 불가");
    }
}
