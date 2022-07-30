package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {
    private static final long 강남역_ID = 1L;
    private static final long 역삼역_ID = 2L;
    private static final long 잠실역_ID = 3L;
    private static final long 남부터미널역_ID = 4L;
    private static final long 양재역_ID = 5L;
    private static final long 어딘가_ID = 999L;
    private static final long 있는역_ID = 998L;
    private static final Station 강남역 = new Station(강남역_ID, "강남역");
    private static final Station 역삼역 = new Station(역삼역_ID, "역삼역");
    private static final Station 잠실역 = new Station(잠실역_ID, "잠실역");
    private static final Station 남부터미널역 = new Station(남부터미널역_ID, "남부터미널역");
    private static final Station 양재역 = new Station(양재역_ID, "남부터미널역");
    private static final Station 어딘가 = new Station(어딘가_ID, "어딘가");
    private static final Station 있는역 = new Station(있는역_ID, "있는역");
    private static final Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);
    private static final Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);
    private static final Section 강남역_남부터미널역_구간 = new Section(강남역, 남부터미널역, 2);
    private static final Section 남부터미널역_양재역 = new Section(남부터미널역, 양재역, 2);
    private static final Section 양재역_잠실역 = new Section(양재역, 잠실역, 1);
    private static final Section 떨어져있는_구간 = new Section(어딘가, 있는역, 999);
    private static final Line 신분당선 = new Line(1L, "신분당선", "red", 강남역_역삼역_구간);
    private static final Line 분당선 = new Line(2L, "분당선", "red", 강남역_남부터미널역_구간);
    private static final Line 외딴선 = new Line(2L, "외딴선", "black", 떨어져있는_구간);
    private static final List<Line> 노선_리스트 = Arrays.asList(신분당선, 분당선, 외딴선);

    @BeforeEach
    void setUp() {
        신분당선.getSections().addSection(역삼역_잠실역_구간);
        분당선.getSections().addSection(남부터미널역_양재역);
        분당선.getSections().addSection(양재역_잠실역);
    }

    @Test
    @DisplayName("출발 역과 도착 역을 입력받아 최적의 경로를 조회한다.")
    void test2() {
        PathFinder finder = new PathFinder(강남역, 잠실역, 노선_리스트);
        List<Station> path = finder.getPath();

        assertAll(
            () -> assertThat(path).startsWith(강남역),
            () -> assertThat(path).endsWith(잠실역)
        );
    }

    @Test
    @DisplayName("출발 역과 도착역이 최단 거리가 되는 경로를 조회한다.")
    void test4() {
        // given
        PathFinder finder = new PathFinder(강남역, 잠실역, 노선_리스트);

        // when & then
        assertThat(finder.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("최단 경로와 최단 거리를 출력한다.")
    void test5() {
        // given
        PathFinder finder = new PathFinder(강남역, 잠실역, 노선_리스트);

        // when & then
        assertThat(finder.getPath()).hasSize(4);
    }

    @Test
    @DisplayName("출발역과 도착역을 같으면 예외가 발생합니다.")
    void test6() {
        Station sourceId = 강남역;
        Station targetId = 강남역;

        assertThatThrownBy(
            () -> new PathFinder(sourceId, targetId, 노선_리스트)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우를 조회하면 예외가 발생합니다.")
    void test7() {
        Station sourceId = 강남역;
        Station targetId = 어딘가;

        assertThatThrownBy(
            () -> new PathFinder(sourceId, targetId, 노선_리스트)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외가 발생합니다.")
    void test8() {
        Station sourceId = 강남역;
        Station 존재하지_않는역 = new Station(928L, "존재하지 않는역");

        assertThatThrownBy(
            () -> new PathFinder(sourceId, 존재하지_않는역, 노선_리스트)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
