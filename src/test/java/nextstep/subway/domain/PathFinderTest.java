package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.applicaion.exception.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("최단 경로 조회 도메인 테스트")
class PathFinderTest {

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    Line 이호선;
    Line 신분당선;
    Line 삼호선;

    List<Line> 노선_모음 = new ArrayList<>();


    /**
     * Given 구간에 역을 등록한다.
     */
    @BeforeEach
    void setUp(){
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");


        이호선 = new Line("이호선","이호선 컬러");
        이호선.addSection(Section.of(교대역,강남역,100));

        신분당선 = new Line("신분당선","신분당선 컬러");
        신분당선.addSection(Section.of(강남역,양재역,10));


        삼호선 = new Line("삼호선","삼호선 컬러");
        삼호선.addSection(Section.of(양재역,남부터미널역,50));
        삼호선.addSection(Section.of(남부터미널역,교대역,10));

        노선_모음.add(이호선);
        노선_모음.add(신분당선);
        노선_모음.add(삼호선);
    }

    /**             100
     * 교대역    --- *2호선* ---   강남역
     * |                        |
* 10 * *3호선*                   *신분당선*      10
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     *              50
     */

    /**
     * Given 노선을 생성한다.
     * When 노선의 최단 경로를 요청한다.
     * Then 최단 경로를 응답한다.
     */
    @DisplayName("최단 경로를 응답한다")
    @Test
    void 최단_경로를_응답한다() {
        //given

        //when
        PathResponse path = PathFinder.findPath(노선_모음, 교대역, 양재역);

        //then
        Assertions.assertThat(path.getStations()).hasSize(3);
        assertTrue(path.getStations().contains(StationResponse.of(남부터미널역)));

    }

    /**
     * Given 노선을 생성한다.
     * When 출발지와 도착지가 같은 최단 경로를 요청한다.
     * Then 예외를 발생한다
     */
    @DisplayName("출발지와 도착지가 같으면 예외")
    @Test
    void 출발지와_도착지가_같으면_예외_발생() {
        //given

        //when //then

        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> {
            PathFinder.findPath(노선_모음, 교대역, 교대역);
        });
    }
}
