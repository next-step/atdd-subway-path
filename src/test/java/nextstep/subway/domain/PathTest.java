package nextstep.subway.domain;

import static nextstep.subway.unit.LineUnitSteps.노선_구간_추가;
import static nextstep.subway.unit.StationUnitSteps.역_추가;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathTest {

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    Station 교대역;
    Station 강남역;
    Station 남부터미널역;
    Station 양재역;

    Path path;

    @BeforeEach
    void setUp() {
        이호선 = new Line(1L, "2호선", "green");
        삼호선 = new Line(2L, "삼호선", "orange");
        신분당선 = new Line(3L, "신분당선", "red");

        교대역 = 역_추가(1L, "교대역");
        강남역 = 역_추가(2L, "강남역");
        남부터미널역 = 역_추가(3L, "남부터미널역");
        양재역 = 역_추가(4L, "양재역");

        노선_구간_추가(이호선, 교대역, 강남역, 10);
        노선_구간_추가(삼호선, 교대역, 남부터미널역, 3);
        노선_구간_추가(삼호선, 남부터미널역, 양재역, 2);
        노선_구간_추가(신분당선, 강남역, 양재역, 5);

        path = new Path(List.of(이호선, 삼호선, 신분당선), 교대역, 양재역);
    }

    /**
        * 교대역    --- *2호선* ---   강남역
         * |                        |
         * *3호선*                   *신분당선*
        * |                        |
        * 남부터미널역  --- *3호선* ---   양재
     */
    @Test
    @DisplayName("최단거리의 역 정보들을 조회")
    void shortestPath() {
        final List<Station> shortestPath = path.getShortestPath();
        assertThat(shortestPath).hasSize(3)
            .containsExactly(교대역, 남부터미널역, 양재역);
    }

    @Test
    @DisplayName("최단거리의 총 합을 조회")
    void shortestDistance() {
        //when
        final int totalDistance = path.getTotalDistance();

        //then
        assertThat(totalDistance).isEqualTo(5);
    }

}