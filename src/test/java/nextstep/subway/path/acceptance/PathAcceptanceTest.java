package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;

@DisplayName("경로 관련 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private LineRequest 신분당선_요청;
    private LineRequest 이호선_요청;
    private LineRequest 삼호선_요청;
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;

    @BeforeEach
    public void beforeEach() {
        super.setUp();
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선_요청 = 지하철_노선_요청("신분당선", "bg-red-600", 강남역, 양재역, 10);
        이호선_요청 = 지하철_노선_요청("이호선", "bg-red-600", 교대역, 강남역, 10);
        삼호선_요청 = 지하철_노선_요청("삼호선", "bg-red-600", 교대역, 양재역, 5);

        신분당선 = 지하철_노선_등록되어_있음(신분당선_요청).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(이호선_요청).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(삼호선_요청).as(LineResponse.class);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("최단 경로를 조회한다")
    @Test
    void findShortestPath() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .params("source",1)
                .params("target",6)
                .when().get("/paths")
                .then().log().all().extract();
        
        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
