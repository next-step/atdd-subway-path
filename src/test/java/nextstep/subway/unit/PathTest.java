package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathTest {

    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    private Station 강남역;
    private Station 교대역;
    private Station 양재역;
    private Station 남부터미널역;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        신분당선 = new Line("신분당선", "red");
        이호선 = new Line("이호선", "red");
        삼호선 = new Line("삼호선", "red");

        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        신분당선.addSection(new Section(강남역, 양재역, 10));
        이호선.addSection(new Section(강남역, 교대역, 10));
        삼호선.addSection(new Section(교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(남부터미널역, 양재역, 3));
    }

    @DisplayName("가장 짧은 경로 조회")
    @Test
    void testFindShortestPath(){
        // given
        List<Line> lines = List.of(신분당선, 이호선, 삼호선);
        Station source = 교대역;
        Station target = 양재역;
        Path path = new Path(source, target, lines);

        // when
        path.findShortestPath();

        // then
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(5);
    }

}