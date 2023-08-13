package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import nextstep.subway.acceptance.section.LineSectionFixture;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.line.LineFixture.지하철_노선_생성_ID;
import static nextstep.subway.acceptance.line.LineFixture.지하철_노선_생성_요청서;
import static nextstep.subway.acceptance.section.LineSectionFixture.지하철_구간_생성;
import static nextstep.subway.acceptance.station.StationFixture.지하철역_생성_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 노원역;
    private Long 창동역;
    private Long 존재하지_않는_역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 사호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     *
     *
     * 노원역  --- 창동역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        교대역 = 지하철역_생성_ID("교대역");
        강남역 = 지하철역_생성_ID("강남역");
        양재역 = 지하철역_생성_ID("양재역");
        남부터미널역 = 지하철역_생성_ID("남부터미널역");
        노원역 = 지하철역_생성_ID("노원역");
        창동역 = 지하철역_생성_ID("창동역");

        이호선 = 지하철_노선_생성_ID(지하철_노선_생성_요청서("2호선", "green", 교대역, 강남역, 10));
        신분당선 = 지하철_노선_생성_ID(지하철_노선_생성_요청서("신분당선", "red", 강남역, 양재역, 10));
        삼호선 = 지하철_노선_생성_ID(지하철_노선_생성_요청서("3호선", "orange", 교대역, 남부터미널역, 2));

        지하철_구간_생성(삼호선, LineSectionFixture.구간_생성_요청서(남부터미널역, 양재역, 3));
    }

    @DisplayName("정상 경우")
    @Nested
    class Success {
        @DisplayName("지하철 경로 조회")
        @Test
        void getPath() {
            //when
            var response = 지하철_경로_조회_요청(교대역, 양재역);
            //then
            List<Long> stationIds = 지하철_경로에_있는_역_ID_리스트_조회(response);
            assertThat(stationIds).containsExactly(교대역, 남부터미널역, 양재역);
            assertThat(response.getDistance()).isEqualTo(5);
        }
    }

    @DisplayName("실패 경우")
    @Nested
    class Fail {
        @DisplayName("지하철 경로 조회 - 출발역 도착역이 같은 경우 ")
        @Test
        void case_0() {
            //when
            //then
            지하철_경로_조회_요청_상태값_체크(교대역, 교대역, HttpStatus.BAD_REQUEST);
        }
        @DisplayName("지하철 경로 조회 - 출발역과 도착역이 연결이 되어 있지 않은 경우")
        @Test
        void case_1() {
            //when
            //then
            지하철_경로_조회_요청_상태값_체크(교대역, 창동역, HttpStatus.BAD_REQUEST);
        }

        @DisplayName("지하철 경로 조회 - 존재하지 않은 출발역이나 도착역을 조회 할 경우")
        @Test
        void case_2() {
            //when
            //then
            지하철_경로_조회_요청_상태값_체크(존재하지_않는_역, 창동역, HttpStatus.BAD_REQUEST);
        }
    }

    private static List<Long> 지하철_경로에_있는_역_ID_리스트_조회(PathResponse response) {
        List<Long> stationIds = response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        return stationIds;
    }

    private static PathResponse 지하철_경로_조회_요청(Long source, Long target) {
        Map<String, Long> params = new HashMap<>();
        params.put("source", source);
        params.put("target", target);
        return RestAssured.given().log().all()
                .params(params)
                .when().get("/paths")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getObject(".", PathResponse.class);
    }

    public static void 지하철_경로_조회_요청_상태값_체크(Long source, Long target, HttpStatus expected) {
        Map<String, Long> params = new HashMap<>();
        params.put("source", source);
        params.put("target", target);
        RestAssured.given().log().all()
                .params(params)
                .when().get("/paths")
                .then().log().all()
                .statusCode(expected.value());
    }

}
