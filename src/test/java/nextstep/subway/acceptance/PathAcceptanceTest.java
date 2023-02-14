package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    @Test
    @DisplayName("경로 조회")
    void findShortestPath() {
        //when 교대에서 양재로 가는 가장 짧은 경로를 조회한다.
        ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(교대역, 양재역);
        //then 남부터미널이 포함되었는지, 거리가 10인지 확인한다.
        assertAll(
                () -> assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("교대역", "남부터미널역", "양재역"),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("조회하려는 경로의 source,target이 서로 같다.")
    void findSourceTargetEqual() {
        //when source와 target 같은 역을 조회한다.
        ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(교대역, 교대역);
        //then 400 에러를 받는다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("경로를 찾을 수 없다.")
    void cannotFind() {
        //given 새로운 노선과 역을 추가한다.
        long 평촌역 = 지하철역_생성_요청("평촌역").jsonPath().getLong("id");
        long 인덕원역 = 지하철역_생성_요청("인덕원역").jsonPath().getLong("id");

        지하철_노선_생성_요청("4호선", "sky", 평촌역, 인덕원역, 10).jsonPath().getLong("id");

        //when 기존 노선의 역에서 새로운 노선의 역으로 가는 경로를 조회한다.
        ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(교대역, 평촌역);
        //then 400 에러를 받는다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("존재하지 않는 역의 경로는 찾을 수 없다.")
    void findNonExistent() {
        //when 존재하지 않는 역으로 가는 경로를 찾는다.
        ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(1, 10);
        //then 400 에러를 받는다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Nested
    @DisplayName("역 개수와 상관없이 더 거리의 경로를 선택한다.")
    class shortestPath{
        Long a;
        Long b;
        @BeforeEach
        void setUp(){
            a = 지하철역_생성_요청("a").jsonPath().getLong("id");
            b= 지하철역_생성_요청("b").jsonPath().getLong("id");

            지하철_노선에_지하철_구간_생성_요청(삼호선, 양재역, a, 5);
            지하철_노선에_지하철_구간_생성_요청(삼호선, a, b, 5);
        }
        @Test
        @DisplayName("정점의 개수가 많지만 더 가까운 경로가 있다.")
        void findMoreStationButShorter() {
            //추가된 마지막 역을 다른 노선에 추가한다. 이때 길이를 더 길게 한다.
            지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, b, 15);
            //when 경로를 조회한다.
            ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(양재역, b);
            //then 경로가 더 짧은 역이 조회된다.
            assertAll(
                    () -> assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("양재역", "a", "b"),
                    () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(10)
            );

        }

        @Test
        @DisplayName("정점의 개수가 적고 더 가까운 경로가 있다.")
        void findLessStationButShorter() {
            //given 추가된 마지막 역을 다른 노선에 추가한다. 이때 길이를 더 짧게 한다.
            지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, b, 5);
            //when 경로를 조회한다.
            ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(양재역, b);
            //then 경로가 더 짧은 역이 조회된다.
            assertAll(
                    () -> assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("양재역", "b"),
                    () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(5)
            );

        }

        @Test
        @DisplayName("정점의 개수가 같지만 거리가 가까운 경로가 있다.")
        void findSameStationsButShorter() {
            //given 역 2개를 생성하고 노선에 추가한다. 다른 노선에 추가된 마지막 역과 새로운 역 1개를 추가한다. 이때 거리를 짧게 한다.
            Long c= 지하철역_생성_요청("c").jsonPath().getLong("id");

            지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, c, 3);
            지하철_노선에_지하철_구간_생성_요청(신분당선, c, b, 3);
            //when 경로를 조회한다.
            ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(양재역, b);
            //then 경로가 더 짧은 역이 조회된다.
            assertAll(
                    () -> assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("양재역", "c","b"),
                    () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(6)
            );

        }
    }

    @Nested
    @DisplayName("환승")
    class transfer {
        Long a;
        Long b;
        Long c;
        Long 십호선;

        @BeforeEach
        void setUp() {
            a = 지하철역_생성_요청("a").jsonPath().getLong("id");
            b = 지하철역_생성_요청("b").jsonPath().getLong("id");
            c = 지하철역_생성_요청("c").jsonPath().getLong("id");
            십호선 = 지하철_노선_생성_요청("10호선", "black", b, c, 2).jsonPath().getLong("id");

            지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, a, 3);
            지하철_노선에_지하철_구간_생성_요청(신분당선, a, b, 3);
        }

        @Test
        @DisplayName("두 번 환승한다.")
        void transferTwice() {
            //when 교대역에서 C역까지의 경로를 검색한다.
            ExtractableResponse<Response> response = PathSteps.지하철_경로_조회_요청(교대역, c);
            //then 환승이 2번 일어난다.
            assertAll(
                    () -> assertThat(response.jsonPath().getList("stations.name", String.class))
                            .containsExactly("교대역", "남부터미널역","양재역","a", "b", "c"),
                    () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(13)
            );

        }
    }


}
