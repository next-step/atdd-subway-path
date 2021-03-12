package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.Extractor;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

public class LineSteps {

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(Map<String, String> params) {
        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return Extractor.post(서비스_호출_경로_생성(null), params);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return Extractor.get(서비스_호출_경로_생성(null));
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse response) {
        return Extractor.get(서비스_호출_경로_생성(response.getId()));
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return Extractor.get(uri);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> response, Map<String, String> params) {
        String uri = response.header("Location");

        return Extractor.put(uri, params);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return Extractor.delete(uri);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId() + "");
        params.put("downStationId", downStation.getId() + "");
        params.put("distance", distance + "");

        return Extractor.post(format("/lines/%s/sections", line.getId()), params);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_제외_요청(LineResponse line, StationResponse station) {
        return Extractor.delete(format("/lines/%s/sections?stationId=%s", line.getId(), station.getId()));
    }

    public static String 서비스_호출_경로_생성(Long createdId) {
        String path = "/lines";
        if (Objects.nonNull(createdId)) {
            return path + "/" + createdId;
        }

        return path;
    }
}
