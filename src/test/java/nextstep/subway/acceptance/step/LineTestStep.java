package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.dto.LineTestRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTestStep {

    public static LineTestRequest 지하철_노선_요청_신분당선_데이터_생성하기() {
        return LineTestRequest.builder()
                .color("bg-red-600")
                .name("신분당선")
                .upStationId(StationTestStep.지하철역_생성_후_아이디_추출하기("강남역"))
                .downStationId(StationTestStep.지하철역_생성_후_아이디_추출하기("양재역"))
                .distance(4)
                .build();
    }

    public static LineTestRequest 지하철_노선_요청_이호선_데이터_생성하기() {
        return LineTestRequest.builder()
                .color("bg-green-600")
                .name("2호선")
                .upStationId(StationTestStep.지하철역_생성_후_아이디_추출하기("시청역"))
                .downStationId(StationTestStep.지하철역_생성_후_아이디_추출하기("성수역"))
                .distance(48)
                .build();
    }

    public static Long 지하철_노선_생성한_후_아이디_추출하기(LineTestRequest lineTestRequest) {
        ExtractableResponse<Response> response = 지하철_노선을_생성한다(lineTestRequest);
        Integer responseIntegerId = response.jsonPath().get("id");
        return responseIntegerId.longValue();
    }

    public static ExtractableResponse<Response> 지하철_노선을_생성한다(LineTestRequest lineTestRequest) {
        return RestAssured
                .given().log().all()
                .body(lineTestRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_생성_성공_검증하기(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        Integer responseIntegerId = response.jsonPath().get("id");
        Long createdId = responseIntegerId.longValue();
        assertThat(createdId).isGreaterThan(0L);
    }

    public static void 지하철_노선_중복이름_생성_실패_검증하기(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_목록을_조회한다() {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_목록_조회_시_두_노선이_있는지_검증하기(ExtractableResponse<Response> response,
                                                     List<String> colors, List<String> names) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> lineColors = response.jsonPath().getList("color");
        assertThat(lineColors).containsExactly(colors.get(0), colors.get(1));
        List<String> lineNames = response.jsonPath().getList("name");
        assertThat(lineNames).containsExactly(names.get(0), names.get(1));
        List<List<Object>> lineStationsList = response.jsonPath().getList("stations");
        assertThat(lineStationsList).hasSizeGreaterThan(0);
    }

    public static void 지하철_노선_목록_조회_시_두_노선이_있는지_검증하기(ExtractableResponse<Response> response,
                                                     List<LineTestRequest> lineTestRequests) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> lineColors = response.jsonPath().getList("color");
        assertThat(lineColors).containsExactlyElementsOf(lineTestRequests.stream()
                .map(LineTestRequest::getColor)
                .collect(Collectors.toList()));
        List<String> lineNames = response.jsonPath().getList("name");
        assertThat(lineNames).containsExactlyElementsOf(lineTestRequests.stream()
                .map(LineTestRequest::getName)
                .collect(Collectors.toList()));

        List<List<Integer>> lineStationsNestedIds = response.jsonPath().getList("stations.id");
        List<Long> lineStationsIds = lineStationsNestedIds.stream()
                .flatMap(List::stream)
                .map(Integer::longValue)
                .collect(Collectors.toList());
        List<Long> expectedStationsIds = lineTestRequests.stream()
                .map(LineTestRequest::getUpStationId).collect(Collectors.toList());
        expectedStationsIds.addAll(lineTestRequests.stream()
                .map(LineTestRequest::getDownStationId)
                .collect(Collectors.toList()));
        assertThat(lineStationsIds).containsAll(expectedStationsIds);
    }

    public static ExtractableResponse<Response> 지하철_노선을_조회한다(Long id) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_조회_성공_검증하기(ExtractableResponse<Response> response,
                                         Long lineId, LineTestRequest lineTestRequest) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Integer receivedId = response.jsonPath().get("id");
        assertThat(receivedId.longValue()).isEqualTo(lineId);
        String receivedColor = response.jsonPath().get("color");
        assertThat(receivedColor).isEqualTo(lineTestRequest.getColor());
        String receivedName = response.jsonPath().get("name");
        assertThat(receivedName).isEqualTo(lineTestRequest.getName());
        List<Long> receivedStationsIds = response.jsonPath().getList("stations.id", Long.class);
        assertThat(receivedStationsIds)
                .containsExactly(lineTestRequest.getUpStationId(), lineTestRequest.getDownStationId());
    }

    public static ExtractableResponse<Response> 지하철_노선을_수정한다(Long lineId, String lineColor, String lineName) {
        Map<String, String> body = new HashMap<>();
        body.put("color", lineColor);
        body.put("name", lineName);

        return RestAssured
                .given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_수정_성공_검증하기(ExtractableResponse<Response> response,
                                         Long createdId, String updatedColor, String updatedName) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> updatedResponse = 지하철_노선을_조회한다(createdId);
        String receivedColor = updatedResponse.jsonPath().get("color");
        assertThat(receivedColor).isEqualTo(updatedColor);
        String receivedName = updatedResponse.jsonPath().get("name");
        assertThat(receivedName).isEqualTo(updatedName);
    }

    public static ExtractableResponse<Response> 지하철_노선을_삭제한다(Long lineId) {
        return RestAssured
                .given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선_삭제_성공_검증하기(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> linesResponse = 지하철_노선_목록을_조회한다();
        List<Object> lines = linesResponse.jsonPath().getList("$");
        assertThat(lines).hasSize(0);
    }
}
