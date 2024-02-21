package nextstep.subway.unit;

import nextstep.config.annotations.ApplicationTest;
import nextstep.config.fixtures.StationFixture;
import nextstep.subway.application.LineService;
import nextstep.subway.application.PathFinder;
import nextstep.subway.application.PathService;
import nextstep.subway.application.dto.PathResult;
import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ApplicationTest
public class PathServiceMockTest {

    PathService pathService;

    @Mock
    LineService lineService;

    @Mock
    PathFinder pathFinder;

    @BeforeEach
    void 서비스_객체_생성() {
        pathService = new PathService(lineService, pathFinder);
    }

    @Nested
    class 지하철_경로 {
        List<Line> 모든_노선_목록;

        Line 이호선;
        Line 신분당선;
        Line 삼호선;
        Line 사호선;

        Station 교대;
        Station 강남;
        Station 양재;
        Station 남부터미널;
        Station 정왕;
        Station 오이도;

        Long 교대역_번호;
        Long 강남역_번호;
        Long 양재역_번호;
        Long 남부터미널역_번호;
        Long 정왕역_번호;
        Long 오이도역_번호;


        @BeforeEach
        void 사전_노선_설정() {
            이호선 = new Line("이호선", "green");
            신분당선 = new Line("신분당선", "red");
            삼호선 = new Line("삼호선", "orange");
            사호선 = new Line("사호선", "blue");

            모든_노선_목록 = List.of(이호선, 신분당선, 삼호선, 사호선);

            교대역_번호 = 1L;
            강남역_번호 = 2L;
            양재역_번호 = 3L;
            남부터미널역_번호 = 4L;
            정왕역_번호 = 5L;
            오이도역_번호 = 6L;

            교대 = StationFixture.교대;
            ReflectionTestUtils.setField(교대, "id", 교대역_번호);

            강남 = StationFixture.강남;
            ReflectionTestUtils.setField(강남, "id", 강남역_번호);

            양재 = StationFixture.양재;
            ReflectionTestUtils.setField(양재, "id", 양재역_번호);

            남부터미널 = StationFixture.남부터미널;
            ReflectionTestUtils.setField(남부터미널, "id", 남부터미널역_번호);

            정왕 = StationFixture.정왕;
            ReflectionTestUtils.setField(정왕, "id", 정왕역_번호);

            오이도 = StationFixture.오이도;
            ReflectionTestUtils.setField(오이도, "id", 오이도역_번호);

            이호선.addSection(new Section(교대, 강남, 10, 이호선));
            신분당선.addSection(new Section(강남, 양재, 10, 신분당선));
            삼호선.addSection(new Section(교대, 남부터미널, 2, 삼호선));
            삼호선.addSection(new Section(남부터미널, 양재, 3, 삼호선));
            사호선.addSection(new Section(정왕, 오이도, 10, 사호선));
        }

        /**
         * Given 지하철 노선을 생성하고, 구간을 추가한다.
         * When  출발역과 도착역을 통해 경로를 조회할 경우
         * Then  최단거리의 존재하는 역 목록과 최단 거리 값을 확인할 수 있다.
         */
        @Test
        void 강남역에서_남부터미널역까지_경로_조회() {
            // given
            when(lineService.findStation(강남역_번호)).thenReturn(강남);
            when(lineService.findStation(남부터미널역_번호)).thenReturn(남부터미널);
            when(lineService.findAllLines()).thenReturn(모든_노선_목록);
            when(pathFinder.calculateShortestPath(모든_노선_목록, 강남, 남부터미널)).thenReturn(new PathResult(List.of(강남, 교대, 남부터미널), 12));

            // when
            PathResponse 경로_조회_응답 = pathService.findShortestPath(new PathRequest(강남역_번호, 남부터미널역_번호));

            // then
            assertThat(경로_조회_응답).usingRecursiveComparison()
                    .isEqualTo(new PathResponse(List.of(강남, 교대, 남부터미널), 12));
        }

        /**
         * Given 지하철 노선을 생성하고, 구간을 추가한다.
         * When  출발역과 도착역을 통해 경로를 조회할 경우
         * Then  최단거리의 존재하는 역 목록과 최단 거리 값을 확인할 수 있다.
         */
        @Test
        void 교대역에서_양재역까지_경로_조회() {
            // given
            when(lineService.findStation(교대역_번호)).thenReturn(교대);
            when(lineService.findStation(양재역_번호)).thenReturn(양재);
            when(lineService.findAllLines()).thenReturn(모든_노선_목록);
            when(pathFinder.calculateShortestPath(모든_노선_목록, 교대, 양재)).thenReturn(new PathResult(List.of(교대, 남부터미널, 양재), 5));

            // when
            PathResponse 경로_조회_응답 = pathService.findShortestPath(new PathRequest(교대역_번호, 양재역_번호));

            // then
            assertThat(경로_조회_응답).usingRecursiveComparison()
                    .isEqualTo(new PathResponse(List.of(교대, 남부터미널, 양재), 5));
        }

    }
}
