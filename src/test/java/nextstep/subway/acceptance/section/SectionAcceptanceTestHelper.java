package nextstep.subway.acceptance.section;

import static nextstep.subway.acceptance.line.LineAcceptanceTestHelper.노선_단건조회_요청;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import org.springframework.http.MediaType;

public class SectionAcceptanceTestHelper {

    public static ExtractableResponse<Response> 구간_등록_요청(HashMap<String, String> params, Long lineId) {
        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines/"+lineId+"/sections")
                          .then().log().all()
                          .extract();
    }

    public static HashMap<String, String> 구간_파라미터_생성(String upStationId, String downStationId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");
        return params;
    }

    static ExtractableResponse<Response> 구간_제거_요청(String stationId, Long lineId) {
        return RestAssured.given().log().all()
                          .param("stationId", stationId)
                          .contentType(
                              MediaType.APPLICATION_JSON_VALUE)
                          .when().delete("/lines/"+lineId+"/sections")
                          .then().log().all()
                          .extract();
    }

    public static String 노선_하행ID조회(Long lineId) {
        return 노선_단건조회_요청(lineId.toString()).jsonPath().getString("stations[-1].id");

    }
}
