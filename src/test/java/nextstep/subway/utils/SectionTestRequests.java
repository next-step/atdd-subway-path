package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import org.springframework.http.MediaType;

public class SectionTestRequests {

    public static ExtractableResponse<Response> 지하철_구간_등록(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);
        String path = "/lines/" + lineId.toString() + "/sections";
        return RestAssured.given().log().all()
                .body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제(Long lineId, Long stationId) {
        String path = "/lines/" + lineId.toString() + "/sections?stationId=" + stationId.toString();
        return RestAssured.given().log().all()
                .when().delete(path)
                .then().log().all()
                .extract();
    }
}
