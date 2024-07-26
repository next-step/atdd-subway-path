package nextstep.subway.acceptance;

import nextstep.subway.line.payload.AddSectionRequest;
import org.junit.jupiter.api.*;

import static nextstep.subway.acceptance.LineApiRequest.노선을_생성한다;
import static nextstep.subway.acceptance.SectionApiRequest.구간을_추가한다;
import static nextstep.subway.acceptance.StationApiRequest.역을_생성한다;
import static nextstep.subway.utils.HttpStatusAssertion.assertBadRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("경로 테스트")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* (10) ---    강남역
     * |                                 |
     * *3호선* (2)                   *신분당선* (10)
     * |                                 |
     * 남부터미널역  --- *3호선* (3) ---   양재
     */
    @BeforeEach
    void setUp() {
        super.setUp();
        교대역 = 역을_생성한다("교대역").jsonPath().getLong("id");
        강남역 = 역을_생성한다("강남역").jsonPath().getLong("id");
        양재역 = 역을_생성한다("양재역").jsonPath().getLong("id");
        남부터미널역 = 역을_생성한다("남부터미널역").jsonPath().getLong("id");

        이호선 = 노선을_생성한다("2호선", "green", 교대역, 강남역, 10L).jsonPath().getLong("id");
        신분당선 = 노선을_생성한다("신분당선", "red", 강남역, 양재역, 10L).jsonPath().getLong("id");
        삼호선 = 노선을_생성한다("3호선", "orange", 교대역, 남부터미널역, 2L).jsonPath().getLong("id");
        구간을_추가한다(삼호선, new AddSectionRequest(남부터미널역, 양재역, 3L));
    }

    @DisplayName("조회시")
    @Nested
    class WhenShow {

        @DisplayName("최단거리를 반환한다")
        @Test
        void whenShowShortestPAth() {
            var 결과 = PathApiRequest.최단거리를_반환한다(교대역, 양재역);
            var ids =결과.jsonPath().getList("stations.id" , Long.class);
            assertThat(ids).containsExactly(교대역, 남부터미널역, 양재역);
        }

        @DisplayName("도착역과 출발역이 같은경우 400 코드를 반환한다")
        @Test
        void whenStartAndEndStationSameThenReturn400() {
            //When 출발역과 도착역이 같은경우
            var 조회_결과 = PathApiRequest.최단거리를_반환한다(교대역, 교대역);
            assertBadRequest(조회_결과.statusCode());
        }

        @DisplayName("도착역이나 출발역이 존재하지 않는 경우 400 코드를 반환한다")
        @Test
        void whenStationNonExistThenReturn400() {
            // Given 존재하지 않는 역을
            var 유령역 = -1L;
            // When 도착역이나 출발역으로 조회할 경우
            var 도착역없음 = PathApiRequest.최단거리를_반환한다(교대역, 유령역);
            var 출발역없음 = PathApiRequest.최단거리를_반환한다(유령역, 교대역);
            // Then
            assertAll(() -> {
                assertBadRequest(도착역없음.statusCode());
                assertBadRequest(출발역없음.statusCode());
            });
        }

        @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 400코드를 반환한다")
        @Test
        void whenCutOffStationThenReturn400() {
            // Given 동떨어진 노선 생성후
            var 해운대역 = 역을_생성한다("해운대역").jsonPath().getLong("id");
            var 서면역 = 역을_생성한다("서면역").jsonPath().getLong("id");
            var 부산1호선 = 노선을_생성한다("부산1호선", "red", 해운대역, 서면역, 5L).jsonPath().getLong("id");

            // When 연결되지 않는 경로를 조회하는경우
            var 조회_결과 = PathApiRequest.최단거리를_반환한다(교대역, 해운대역);

            // Then 400코드를 반환한다.
            assertBadRequest(조회_결과.statusCode());
        }

    }

}
