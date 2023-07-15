package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;

import java.util.List;

import static nextstep.subway.utils.AcceptanceTestUtils.get;
import static nextstep.subway.utils.AcceptanceTestUtils.post;

public class LineSteps {

    public static Long 지하철_노선_생성_요청(String name, Long upStationId, Long downStationId, int distance) {
        LineRequest request = new LineRequest(name, "color", upStationId, downStationId, distance);
        return post("/lines", request).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(Long lineId, SectionRequest request) {
        return post("/lines/{id}/sections", lineId, request);
    }

    public static List<String> 지하철_노선_조회_요청_역_이름_목록_반환(Long lineId) {
        return get("/lines/{id}", lineId).jsonPath().get("stations.name");
    }

    public static int 지하철_노선_조회_요청_총_거리_반환(Long lineId) {
        return get("/lines/{id}", lineId).jsonPath().getInt("distance");
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_삭제_요청(Long lineId, Long stationId) {
        return RestAssured
                .given().log().all()
                .queryParam("stationId", stationId)
                .pathParam("id", lineId)
                .when().delete("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

}
