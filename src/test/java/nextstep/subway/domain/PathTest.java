package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로 관련 기능")
class PathTest {

    private Line 삼호선;
    private Line 이호선;
    private Line 신분당선;
    private Line 수인분당선;

    private Station 남부터미널역;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        삼호선 = new Line("3호선", "bg-orange-500");
        이호선 = new Line("2호선", "bg-green-500");
        신분당선 = new Line("신분당선", "bg-red-500");
        수인분당선 = new Line("수인분당선", "bg-yellow-500");

        남부터미널역 = new Station("남부터미널역");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        정자역 = new Station("정자역");
    }

    /**
     * 교대역   --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역 --- *3호선* --- 양재역  --- *수인분당선* ---  정자역
     */
    @DisplayName("시작역과 도착역을 기준으로 최단경로를 반환한다.")
    @Test
    void find() {
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 1));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 100));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 4));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 5));
        수인분당선.addSection(new Section(수인분당선, 양재역, 정자역, 9));
        Path path = new Path(List.of(삼호선, 이호선, 신분당선, 수인분당선));
        Double expected = 19.0;

        GraphPath graphPath = path.find(남부터미널역, 정자역);
        assertAll(
                () -> assertThat(graphPath.getWeight()).isEqualTo(expected),
                () -> assertThat(graphPath.getVertexList()).containsExactly(남부터미널역, 교대역, 강남역, 양재역, 정자역)
        );
    }
}
