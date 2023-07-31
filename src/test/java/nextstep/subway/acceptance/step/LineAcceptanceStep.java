package nextstep.subway.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.line.dto.request.SaveLineRequestDto;
import nextstep.subway.line.dto.request.UpdateLineRequestDto;
import nextstep.subway.support.RestAssuredClient;

public class LineAcceptanceStep {

    private static final String LINE_BASE_URL = Endpoint.LINE_BASE_URL.getUrl();

    /**
     * <pre>
     * 지하철 노선을 생성하는 API를 호출하는 함수
     * </pre>
     *
     * @param line
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_노선_생성을_요청한다(SaveLineRequestDto line) {
        return RestAssuredClient.post(LINE_BASE_URL, line);
    }

    /**
     * <pre>
     * 모든 지하철 노선들을 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_노선_목록_조회를_요청한다() {
        ExtractableResponse<Response> findStationsAllResponse = RestAssuredClient.get(LINE_BASE_URL);
        return findStationsAllResponse;
    }

    /**
     * <pre>
     * 지하철 노선을 id로 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @param id
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_노선_상세_조회를_요청한다(Long id) {
        String path = String.format("%s/%d", LINE_BASE_URL, id);
        return RestAssuredClient.get(path);
    }

    /**
     * <pre>
     * 지하철 노선을 수정하는 API를 호출하는 함수
     * </pre>
     *
     * @param line 수정할 정보가 담긴 노선
     * @param id
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_노선_수정을_요청한다(UpdateLineRequestDto line, Long id) {
        String path = String.format("%s/%d", LINE_BASE_URL, id);
        return RestAssuredClient.put(path, line);
    }

    /**
     * <pre>
     * 지하철 노선을 삭제하는 API를 호출하는 함수
     * </pre>
     *
     * @param id
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철_노선_삭제를_요청한다(Long id) {
        String path = String.format("%s/%d", LINE_BASE_URL, id);
        return RestAssuredClient.delete(path);
    }

}
