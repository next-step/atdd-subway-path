package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;

import static nextstep.subway.utils.AcceptanceTestUtils.*;

public class LineSteps {

    public static LineResponse 지하철_노선_생성_요청(String name, Long upStationId, Long downStationId, int distance) {
        LineRequest request = new LineRequest(name, "color", upStationId, downStationId, distance);
        return post("/lines", request).as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(Long lineId, SectionRequest request) {
        return post("/lines/{id}/sections", lineId, request);
    }

    public static LineResponse 지하철_노선_조회_요청(Long lineId) {
        return get("/lines/{id}", lineId).as(LineResponse.class);
    }

}
