package nextstep.subway.unit;

import nextstep.config.annotations.ApplicationTest;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.application.PathFinder;
import nextstep.subway.application.dto.PathResult;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class PathFinderComponentTest {

    Line 이호선;
    Line 신분당선;
    Line 삼호선;
    Line 사호선;

    List<Line> 모든_노선_목록;

    Long 이호선_아이디;
    Long 신분당선_아이디;
    Long 삼호선_아이디;
    Long 사호선_아이디;

    Station 교대;
    Station 강남;
    Station 양재;
    Station 남부터미널;
    Station 정왕;
    Station 오이도;

    Long 교대역_아이디;
    Long 강남역_아이디;
    Long 양재역_아이디;
    Long 남부터미널역_아이디;
    Long 정왕역_아이디;
    Long 오이도역_아이디;

    @Autowired
    private PathFinder pathFinder;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     * <p>
     * <p>
     * 오이도역 --- *4호선* --- 정왕역
     */
    @BeforeEach
    void 사전_노선_설정() {
        이호선_아이디 = 1L;
        신분당선_아이디 = 2L;
        삼호선_아이디 = 3L;
        사호선_아이디 = 3L;

        이호선 = new Line("이호선", "green");
        ReflectionTestUtils.setField(이호선, "id", 이호선_아이디);

        신분당선 = new Line("신분당선", "red");
        ReflectionTestUtils.setField(신분당선, "id", 신분당선_아이디);

        삼호선 = new Line("삼호선", "orange");
        ReflectionTestUtils.setField(삼호선, "id", 삼호선_아이디);

        사호선 = new Line("사호선", "blue");
        ReflectionTestUtils.setField(사호선, "id", 사호선_아이디);

        모든_노선_목록 = List.of(이호선, 신분당선, 삼호선, 사호선);

        교대역_아이디 = 1L;
        강남역_아이디 = 2L;
        양재역_아이디 = 3L;
        남부터미널역_아이디 = 4L;
        정왕역_아이디 = 5L;
        오이도역_아이디 = 6L;

        교대 = StationFixture.교대;
        ReflectionTestUtils.setField(교대, "id", 교대역_아이디);

        강남 = StationFixture.강남;
        ReflectionTestUtils.setField(강남, "id", 강남역_아이디);

        양재 = StationFixture.양재;
        ReflectionTestUtils.setField(양재, "id", 양재역_아이디);

        남부터미널 = StationFixture.남부터미널;
        ReflectionTestUtils.setField(남부터미널, "id", 남부터미널역_아이디);

        정왕 = StationFixture.정왕;
        ReflectionTestUtils.setField(정왕, "id", 정왕역_아이디);

        오이도 = StationFixture.오이도;
        ReflectionTestUtils.setField(오이도, "id", 오이도역_아이디);

        이호선.addSection(new Section(교대, 강남, 10, 이호선));
        신분당선.addSection(new Section(강남, 양재, 10, 신분당선));
        삼호선.addSection(new Section(교대, 남부터미널, 2, 삼호선));
        삼호선.addSection(new Section(남부터미널, 양재, 3, 삼호선));
        사호선.addSection(new Section(정왕, 오이도, 10, 사호선));
    }

    @Nested
    class findShortestPath {

        /**
         * Given 지하철 노선이 목록이 생성된다.
         * When  출발역과 도착역을 통해 경로를 조회할 경우
         * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
         */
        @Test
        void 강남역에서_남부터미널역까지_경로_조회() {
            // when
            PathResult 경로_조회_결과 = pathFinder.calculateShortestPath(모든_노선_목록, 강남, 남부터미널);

            // then
            assertThat(경로_조회_결과.getStations()).containsExactly(강남, 교대, 남부터미널);
            assertThat(경로_조회_결과.getDistance()).isEqualTo(12);
        }

        /**
         * Given 지하철 노선이 목록이 생성된다.
         * When  출발역과 도착역을 통해 경로를 조회할 경우
         * Then  최단거리의 존재하는 역 목록과 최단거리 값을 확인할 수 있다.
         */
        @Test
        void 교대역에서_양재역까지_경로_조회() {
            // when
            PathResult 경로_조회_결과 = pathFinder.calculateShortestPath(모든_노선_목록, 교대, 양재);

            // then
            assertThat(경로_조회_결과.getStations()).containsExactly(교대, 남부터미널, 양재);
            assertThat(경로_조회_결과.getDistance()).isEqualTo(5);
        }
    }
}
