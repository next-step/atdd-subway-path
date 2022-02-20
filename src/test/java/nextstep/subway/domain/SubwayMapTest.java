package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubwayMapTest {

    private SubwayMap subwayMap;

    Station 강남역;
    Station 역삼역;
    Station 판교역;
    Station 교대역;
    Station 남부역;
    Station 양재역;

    /**       (사호선)       (오호선)
     * (일호선) [강남역] -10- [역삼역]
     *           ㅣ           ㅣ
     *           5            7
     *           ㅣ           ㅣ
     * (이호선) [판교역] -20- [교대역]
     *           ㅣ           ㅣ
     *           6            8
     *           ㅣ           ㅣ
     * (삼호선) [남부역] -30- [양재역]
     */
    @BeforeEach
    void setUp() {
        Line 일호선 = new Line("일호선", "red");
        Line 이호선 = new Line("이호선", "blue");
        Line 삼호선 = new Line("삼호선", "white");
        Line 사호선 = new Line("사호선", "black");
        Line 오호선 = new Line("오호선", "green");

        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        판교역 = new Station("판교역");
        교대역 = new Station("교대역");
        남부역 = new Station("남부역");
        양재역 = new Station("양재역");

        일호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(판교역, 교대역, 20);
        삼호선.addSection(남부역, 양재역, 30);
        사호선.addSection(강남역, 판교역, 5);
        사호선.addSection(판교역, 남부역, 6);
        오호선.addSection(역삼역, 교대역, 7);
        오호선.addSection(교대역, 양재역, 8);

        List<Line> lines = Arrays.asList(일호선, 이호선, 삼호선, 사호선, 오호선);
        subwayMap = new SubwayMap(lines);
    }

    @Test
    @DisplayName("두 역 사이의 최단 경로 계산")
    void 최단_경로_계산() {
         assertThat(subwayMap.getShortestPath(강남역, 교대역)).containsExactly(강남역, 역삼역, 교대역);
    }

    @Test
    @DisplayName("두 역 사이의 최단 거리 계산")
    void 최단_거리_계산() {
         assertThat(subwayMap.getShortestDistance(강남역, 교대역)).isEqualTo(17);
    }

}