package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {
    private static final long 강남역_ID = 1L;
    private static final long 역삼역_ID = 2L;
    private static final long 잠실역_ID = 3L;
    private static final Station 강남역 = new Station(강남역_ID, "강남역");
    private static final Station 역삼역 = new Station(역삼역_ID, "역삼역");
    private static final Station 잠실역 = new Station(잠실역_ID, "잠실역");
    private static final Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);
    private static final Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);
    private static final Line 신분당선 = new Line(1L, "신분당선", "red", 강남역_역삼역_구간);
    private static final List<Line> 노선_리스트 = List.of(신분당선);

    @BeforeEach
    void setUp() {
        신분당선.getSections().addSection(역삼역_잠실역_구간);
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
        assertThat(finder.getDistance()).isEqualTo(20);
    }

    @Test
    @DisplayName("최단 경로와 최단 거리를 출력한다.")
    void test5() {
        // given
        PathFinder finder = new PathFinder(강남역, 잠실역, 노선_리스트);

        // when & then
        assertThat(finder.getPath()).hasSize(3);
    }
}
