package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.http.ContentType;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;

public class PathAcceptanceTest extends BaseAcceptanceTest {
    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 남부터미널역_ID;
    private Long 이호선_ID;
    private Long 신분당선_ID;
    private Long 삼호선_ID;

    /**
     * 교대역    --- *2호선(10)* ---   강남역
     * |                        |
     * *3호선(2)*                   *신분당선(10)*
     * |                        |
     * 남부터미널역  --- *3호선(3)* ---   양재역
     */

    @BeforeEach
    void setUp() {
        교대역_ID = 지하철_역_생성(교대역);
        강남역_ID = 지하철_역_생성(강남역);
        양재역_ID = 지하철_역_생성(양재역);
        남부터미널역_ID = 지하철_역_생성(남부터미널역);
        이호선_ID = 지하철_노선_생성_ID(getRequestParam("이호선", "초록색", 교대역_ID, 강남역_ID, 10));
        신분당선_ID = 지하철_노선_생성_ID(getRequestParam("신분당선", "빨간색", 강남역_ID, 양재역_ID, 10));
        삼호선_ID = 지하철_노선_생성_ID(getRequestParam("삼호선", "주황색", 교대역_ID, 남부터미널역_ID, 2));

        지하철_노선에_지하철_구간_생성_요청(삼호선_ID, new SectionRequest(남부터미널역_ID, 양재역_ID, 3));
    }

    @DisplayName("   given 출발역과 도착역이 주어질 때\n"
                 + "   when 경로를 조회하면\n"
                 + "   then 출발역과 도착역 사이 역 목록과\n"
                 + "   and  출발역과 도착역 사이 거리를 조회할 수 있다.")
    @Test
    void 경로를_조회하면_출발역과_도착역_사이_역_목록과_거리를_조회할_수_있다() {
        //given
        Long 출발역 = 교대역_ID;
        Long 도착역 = 양재역_ID;

        //when
        PathResponse pathResponse = 지하철_경로_조회(출발역, 도착역);

        //then
        List<StationResponse> stationResponses = pathResponse.getStations();
        assertAll(
            () -> assertThat(stationResponses).hasSize(3),
            () -> assertThat(stationResponses).extracting(StationResponse::getId).containsExactly(교대역_ID, 남부터미널역_ID, 양재역_ID),
            () -> assertThat(pathResponse.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("given 출발역과 도착역이 주어질 때\n"
                 + "    when 경로를 조회하는데\n"
                 + "    and  출발역과 도착역이 동일하면\n"
                 + "    then 예외를 반환한다.")
    @Test
    void 경로를_조회할_때_출발역과_도착역이_동일하면_예외를_반환한다() {
        //given
        Long 출발역 = 교대역_ID;
        Long 도착역 = 교대역_ID;

        //when
        given()
            .queryParam("sourceId", 출발역)
            .queryParam("targetId", 도착역)
            .accept(ContentType.JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @DisplayName("given 출발역과 도착역이 주어질 때\n"
                 + "    when 경로를 조회하는데\n"
                 + "    and  출발역과 도착역이 연결되어 있지 않으면\n"
                 + "    then 예외를 반환한다.")
    @Test
    void 경로를_조회할_때_출발역과_도착역이_연결되어있지_않으면_예외를_반환한다() {
        //given
        Long 출발역 = 교대역_ID;
        Long 도착역 = 교대역_ID;

        //when
        given()
            .queryParam("sourceId", 출발역)
            .queryParam("targetId", 도착역)
            .accept(ContentType.JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @DisplayName("given 출발역과 도착역이 주어질 때\n"
                 + "    when 경로를 조회하는데\n"
                 + "    and  출발역 또는 도착역이 존재하지 않으면\n"
                 + "    then 예외를 반환한다.")
    @Test
    void 경로를_조회할_때_출발역_또는_도착역이_존재하지_않으면_예외를_반환한다() {
        //given
        Long 왕십리역_ID = 지하철_역_생성(왕십리역);

        Long 출발역 = 교대역_ID;
        Long 도착역 = 왕십리역_ID; //  경로에 존재하지 않는 역 ID

        //when
        given()
            .queryParam("sourceId", 출발역)
            .queryParam("targetId", 도착역)
            .accept(ContentType.JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
