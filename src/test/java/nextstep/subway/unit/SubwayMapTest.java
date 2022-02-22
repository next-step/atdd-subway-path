package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayMap;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class SubwayMapTest {
    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    private List<Line> lines;

    /**
     * 교대역    --- *2호선*(5) ---   강남역
     * |                              |
     * *3호선*(2)                   *신분당선*(10)
     * |                              |
     * 남부터미널역  --- *3호선*(3) ---   양재
     *
     * 교대 - 강남 - 양재 15
     * 교대 - 남부터미널 - 양재 5
     */

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");

        신분당선 = new Line("신분당선", "red");
        이호선 = new Line("2호선", "green");
        삼호선 = new Line("3호선", "orange");

        신분당선.addSection(강남역, 양재역, 10);
        이호선.addSection(교대역, 강남역, 5);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        lines = Lists.newArrayList(신분당선, 이호선, 삼호선);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void findPath() {
        // given
        Station source = 교대역;
        Station target = 양재역;
        SubwayMap subwayMap = new SubwayMap(lines);

        // when
        Path path = subwayMap.findPath(source, target);

        // then
        assertThat(path.stations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.totalDistance()).isEqualTo(5);
    }

    @DisplayName("최단 경로 조회 - 경로가 연결되어 있지 않음")
    @Test
    void findPath2() {
        // given
        Station source = 교대역;
        Station target = new Station("강변역");
        SubwayMap subwayMap = new SubwayMap(lines);

        // when & then
        assertThatThrownBy(() -> subwayMap.findPath(source, target))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("최단 경로 조회 - 출발역과 도착역이 같음")
    @Test
    void findPath3() {
        // given
        Station source = 교대역;
        Station target = 교대역;
        SubwayMap subwayMap = new SubwayMap(lines);

        // when
        Path path = subwayMap.findPath(source, target);

        // then
        assertThat(path.stations()).isEmpty();
        assertThat(path.totalDistance()).isEqualTo(0);
    }

    @DisplayName("최단 경로 조회 - 반대 경로")
    @Test
    void findPath4() {
        // given
        Station source = 양재역;
        Station target = 교대역;
        SubwayMap subwayMap = new SubwayMap(lines);

        // when
        Path path = subwayMap.findPath(source, target);

        // then
        assertThat(path.stations()).containsExactly(양재역, 남부터미널역, 교대역);
        assertThat(path.totalDistance()).isEqualTo(5);
    }
}
