package nextstep.subway.fixture.acceptance.when;

import static nextstep.subway.fixture.acceptance.given.SectionRequestFixture.노선구간추가등록_요청데이터;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class SectionApiFixture {

    public static ExtractableResponse<Response> 지하철_노선_구간_추가_등록(long 지하철역_노선_id,
        long downStationId, long upStationId, int distance) {

        Map<String, Object> params = 노선구간추가등록_요청데이터(downStationId, upStationId, distance);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", 지하철역_노선_id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_삭제(long 지하철역_노선_id,
        long 추가_하행역_id) {

        return RestAssured
            .given().log().all()
            .queryParam("stationId", 추가_하행역_id)
            .when().delete("/lines/{id}/sections", 지하철역_노선_id)
            .then().log().all()
            .extract();
    }
}
