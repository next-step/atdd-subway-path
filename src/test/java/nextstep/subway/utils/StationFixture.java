package nextstep.subway.utils;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;

public class StationFixture {

    public static final String 신사역 = "신사역";
    public static final String 논현역 = "논현역";
    public static final String 신논현역 = "신논현역";
    public static final String 강남역 = "강남역";
    public static final String 양재역 = "양재역";
    public static final String 양재시민의숲역 = "양재시민의숲역";
    public static final String 청계산입구역 = "청계산입구역";
    public static final String 판교역 = "판교역";
    public static final String 정자역 = "정자역";
    public static final String 이매역 = "이매역";
    public static final String 삼동역 = "삼동역";
    public static final String 경기광주역 = "경기광주역";
    public static final String 초월역 = "초월역";
    public static final String 곤지암역 = "곤지암역";

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        return RestAssured.given().log().all()
            .body(new StationRequest(name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_조회_요청() {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long id) {
        return RestAssured.given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/stations/{id}", id)
            .then().log().all()
            .extract();
    }

    public static StationResponse 지하철역_생성(String name) {
        return 지하철역_리스폰_변환(지하철역_생성_요청(name));
    }

    public static StationResponse 지하철역_리스폰_변환(ExtractableResponse<Response> response) {
        return response.response()
            .as(StationResponse.class);
    }
}
