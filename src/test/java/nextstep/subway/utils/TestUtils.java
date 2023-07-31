package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

public class TestUtils {
    public static ExtractableResponse<Response> 이호선_테스트_데이터_생성() {
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        return 지하철_노선_생성_요청("2호선", "green", 강남역, 정자역, 10);
    }

    public static ExtractableResponse<Response> 삼호선_테스트_데이터_생성() {
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        return 지하철_노선_생성_요청("2호선", "green", 강남역, 정자역, 10);
    }
}
