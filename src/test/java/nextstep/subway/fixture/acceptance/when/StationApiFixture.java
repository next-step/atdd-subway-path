package nextstep.subway.fixture.acceptance.when;

import static nextstep.subway.fixture.acceptance.given.StationRequestFixture.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class StationApiFixture {

    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {

        Map<String, String> params = 지하철역_등록_요청_데이터_생성(name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static long 지하철역_생성_요청_후_id_추출(String name) {

        Map<String, String> params = 지하철역_등록_요청_데이터_생성(name);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract()
            .jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철역_리스트_조회() {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }    
    
    public static ExtractableResponse<Response> 지하철역_삭제(long id) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/{id}", id)
                .then().log().all()
                .extract();
    }
}
