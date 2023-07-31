package nextstep.subway.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.station.dto.request.SaveStationRequestDto;
import nextstep.subway.support.RestAssuredClient;

public class StationAcceptanceStep {

    private static final String STATION_BASE_URL = Endpoint.STATION_BASE_URL.getUrl();

    /**
     * <pre>
     * 지하철역을 생성하는 API를 호출하는 함수
     * </pre>
     *
     * @param station
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철역_생성을_요청한다(SaveStationRequestDto station) {
        return RestAssuredClient.post(STATION_BASE_URL, station);
    }

    /**
     * <pre>
     * 모든 지하철역들을 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철역_목록_조회를_요청한다() {
        return RestAssuredClient.get(STATION_BASE_URL);
    }

    /**
     * <pre>
     * 지하철역을 삭제하는 API를 호출하는 함수
     * </pre>
     *
     * @param stationId
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 지하철역_삭제을_요청한다(Long stationId) {
        String path = String.format("%s/%d", STATION_BASE_URL, stationId);
        return  RestAssuredClient.delete(path);
    }

}
