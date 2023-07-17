package nextstep.subway.acceptance.commonStep;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionStep {

    public static ExtractableResponse<Response> 지하철구간_생성(Long lineId, Long upStationId,Long downStationId,Long distance) {

        Map<String, String> params = new HashMap<>();
        params.put("upStationId",String.valueOf(upStationId));
        params.put("downStationId",String.valueOf(downStationId));
        params.put("distance",String.valueOf(distance));

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines/"+lineId+"/sections")
                        .then().log().all()
                        .extract();

        return response;
    }

    public static ExtractableResponse<Response> 지하철구간_삭제(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all().extract();
    }


}
