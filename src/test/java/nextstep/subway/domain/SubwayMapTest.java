package nextstep.subway.domain;

import nextstep.subway.domain.vo.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubwayMapTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;


    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("이호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("삼호선", "orange");
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

    }

    @DisplayName("경로를 찾을 수 있다.")
    @Test
    void findPath() {
        //given
        SubwayMap map = new SubwayMap(List.of(이호선, 신분당선, 삼호선));

        //when
        Path path = map.shortestPath(교대역, 양재역);

        //then
        assertThat(path.getDistance()).isEqualTo(5);
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
    }

}