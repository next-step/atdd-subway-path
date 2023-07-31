package nextstep.subway.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.line.dto.request.SaveLineSectionRequest;
import nextstep.subway.support.RestAssuredClient;

public class LineSectionAcceptanceStep {

    private static final String LINE_BASE_URL = Endpoint.LINE_BASE_URL.getUrl();

    /**
     * <pre>
     * 지하철 노선 구간을 생성하는 API를 호출하는 함수
     * </pre>
     *
     * @param lineSection
     * @param lineId
     * @param
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_구간_생성을_요청한다(SaveLineSectionRequest lineSection, Long lineId) {
        String path = String.format("%s/%d/sections", LINE_BASE_URL, lineId);
        return RestAssuredClient.post(path, lineSection);
    }

    /**
     * <pre>
     * 지하철역 id로
     * 지하철 노선 구간을 삭제하는 API를 호출하는 함수
     * </pre>
     *
     * @param lineId
     * @param stationId
     * @param
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_구간_삭제을_요청한다(Long lineId, Long stationId) {
        String path = String.format("%s/%d/sections?stationId=%d", LINE_BASE_URL, lineId, stationId);
        return RestAssuredClient.delete(path);
    }

}
