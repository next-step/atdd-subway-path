package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineTestRequest;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.commons.AssertionsUtils.*;
import static nextstep.subway.commons.RestAssuredUtils.*;
import static nextstep.subway.station.acceptance.StationUtils.지하철역_생성요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LineUtils {

    private LineUtils() {}

    public static long getLineId(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class).getId();
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(LineTestRequest request) {
        long upStationId = 지하철역_생성요청(request.getUpStationName()).jsonPath().getLong("id");
        long downStationId = 지하철역_생성요청(request.getDownStationName()).jsonPath().getLong("id");

        Map<String, String> params = new HashMap<>();
        params.put("name", request.getLineName());
        params.put("color", request.getLineColor());
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(request.getDistance()));

        return post("/lines", params);
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(LineRequest request) {

        Map<String, String> params = new HashMap<>();
        params.put("name", request.getName());
        params.put("color", request.getColor());
        params.put("upStationId", String.valueOf(request.getUpStationId()));
        params.put("downStationId", String.valueOf(request.getDownStationId()));
        params.put("distance", String.valueOf(request.getDistance()));

        return post("/lines", params);
    }

    public static void 지하철노선_생성_성공(ExtractableResponse<Response> response) {
        생성요청_성공(response);
    }

    public static ExtractableResponse<Response> 지하철노선_단건조회_요청(Long id) {
        return get("/lines/" + id);
    }

    public static void 지하철노선_단건조회_성공(ExtractableResponse<Response> response) {
        조회요청_성공(response);
        assertThat(response.jsonPath().getList("stations").size()).isEqualTo(2);
    }

    public static ExtractableResponse<Response> 지하철노선_목록조회_요청() {
        return get("/lines/");
    }

    public static void 지하철노선목록_조회_성공(ExtractableResponse<Response> response) {
        조회요청_성공(response);
        assertThat(response.jsonPath().getList("id")).hasSize(2);
    }

    public static ExtractableResponse<Response> 지하철노선_수정_요청(Long id, Map<String, String> params) {
       return put("/lines/" + id, params);
    }

    public static void 지하철노선_수정_성공(ExtractableResponse<Response> response, Map<String, String> params) {
        수정요청_성공(response);
        assertThat(jsonPath("$.name", is(params.get("name"))));
        assertThat(jsonPath("$.color", is(params.get("color"))));
    }

    public static ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return delete("/lines/" + id);
    }

}
