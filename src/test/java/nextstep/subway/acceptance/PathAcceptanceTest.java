package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 경로 탐색")
public class PathAcceptanceTest extends AcceptanceTest {
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
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");;
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");;

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * given 지하철 노선 구간 정보를 바탕으로
     * when 시작역과 도착역을 가지고 경로 조회를 할 경우
     * then 경로가 정상적으로 조회 된다
     */
    @DisplayName("지하철 경로 조회 기능 테스트")
    @Test
    void retrievePath() {
        //given
        Long 출발역 = 교대역;
        Long 도착역 = 양재역;

        //when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(출발역, 도착역);

        //then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> path = response.jsonPath().getList("stations.id", Long.class);
        Assertions.assertThat(path.size()).isEqualTo(3);
        Assertions.assertThat(path).containsExactly(교대역, 남부터미널역, 양재역);
    }


    ExtractableResponse<Response> 지하철_경로_조회_요청(Long source, Long target) {
        Map<String, String> params = new HashMap<>();
        params.put("source", source.toString());
        params.put("target", target.toString());

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={source}&target={target}", source, target)
                .then().log().all().extract();
    }

    /**
     * given 지하철 노선 구간 정보를 바탕으로
     * when 존재하지 않는 시작역과 도착역을 가지고 경로 조회를 할 경우
     * then 경로 조회에 실패한다
     */
    @DisplayName("존재하지 않는 역을 바탕으로 지하철 경로 조회 실패 테스트")
    @Test
    void retrievePathFailNotExist() {
        //given
        Long 없는역 = 9999L;
        Long 도착역 = 양재역;

        //when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(없는역, 도착역);

        //then
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    /**
     * given 지하철 노선 구간 정보를 바탕으로
     * when 이어지지 않은 시작역과 도착역을 가지고 경로 조회를 할 경우
     * then 경로 조회에 실패한다
     */
    @DisplayName("이어지지 않은 역을 바탕으은 지하철 경로 조회 실패 테스트")
    @Test
    void retrievePathFailNotConnected() {
        //given
        Long 이어지지않은역 = 지하철역_생성_요청("이어지지않은역").jsonPath().getLong("id");
        Long 도착역 = 양재역;

        //when
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(이어지지않은역, 도착역);

        //then
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }
}
