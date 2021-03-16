package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private StationResponse STATION_1;
    private StationResponse STATION_2;
    private StationResponse STATION_3;
    private StationResponse STATION_4;

    private final int DISTANCE = 10;

    private LineResponse line;

    @BeforeEach
    void before() {
        STATION_1 = 지하철역_생성_요청("강남역");
        STATION_2 = 지하철역_생성_요청("신촌역");
        STATION_3 = 지하철역_생성_요청("신림역");
        STATION_4 = 지하철역_생성_요청("삼성역");

        String COLOR_1 = "blue";
        String LINE_NAME_1 = "1호선";
        LineRequest request = new LineRequest(LINE_NAME_1, COLOR_1, STATION_1.getId(), STATION_2.getId(), DISTANCE);
        line = 노선_생성_요청(request);
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addLineSection() {
        // when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(line, STATION_2.getId(), STATION_3.getId(), DISTANCE);

        // then
        지하철_노선에_지하철역_등록됨(response);
        LineResponse mockLine = 노선_조회_요청(response);
        지하철_노선에_지하철역_순서_정렬됨(mockLine, Arrays.asList(STATION_1, STATION_2, STATION_3));
    }

    @DisplayName("역 사이에 새로운 구간을 추가한다.")
    @Test
    void addSectionBetweenStations() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(line, STATION_1.getId(), STATION_3.getId(), 5);

        //then
        지하철_노선에_지하철역_등록됨(response);

        LineResponse mockLine = 노선_조회_요청(response);
        지하철_노선에_지하철역_순서_정렬됨(mockLine, Arrays.asList(STATION_1, STATION_3, STATION_2));
    }

    @DisplayName("새로운 역을 상행역으로하여 구간을 추가한다.")
    @Test
    void addSectionPreviousUpStation() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(line, STATION_3.getId(), STATION_1.getId(), 5);
        지하철_노선에_지하철역_등록됨(response);

        LineResponse mockLine = 노선_조회_요청(response);
        //then
        지하철_노선에_지하철역_순서_정렬됨(mockLine, Arrays.asList(STATION_3, STATION_1, STATION_2));
    }

    @DisplayName("이미 등록된 상행역과 하행역 구간을 등록한다.")
    @Test
    void addSectionDuplicateAllStation() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(line, STATION_1.getId(), STATION_2.getId(), 10);

        //then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("역 사이에 기존 구간보다 길이가 긴 구간을 추가한다.")
    @Test
    void addSectionBetweenStationsWithInvalidDistance() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(line, STATION_1.getId(), STATION_3.getId(), 15);

        //then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("상행역과 하행역에 모두 포함되지 않는 구간을 추가한다.")
    @Test
    void addSectionNotIncludeStations() {
        //when
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(line, STATION_3.getId(), STATION_4.getId(), 10);

        //then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 첫번째 지하철역을 제외한다.")
    @Test
    void removeFirstLineSection() {
        // given
        노선에_지하철역_등록_요청(line, STATION_2.getId(), STATION_3.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> removeResponse = 노선에_지하철역_제외_요청(line, STATION_1);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        LineResponse response = 노선_조회_요청(removeResponse);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(STATION_2, STATION_3));
    }

    @DisplayName("지하철 노선에 등록된 마지막 지하철역을 제외한다.")
    @Test
    void removeLineSection() {
        // given
        노선에_지하철역_등록_요청(line, STATION_2.getId(), STATION_3.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> removeResponse = 노선에_지하철역_제외_요청(line, STATION_3);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        LineResponse response = 노선_조회_요청(removeResponse);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(STATION_1, STATION_2));
    }

    @DisplayName("지하철 노선에 등록된 가운데 지하철역을 제외한다.")
    @Test
    void removeMiddleLineSection() {
        // given
        노선에_지하철역_등록_요청(line, STATION_2.getId(), STATION_3.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> removeResponse = 노선에_지하철역_제외_요청(line, STATION_2);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        LineResponse response = 노선_조회_요청(removeResponse);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(STATION_1, STATION_3));
    }

    @DisplayName("지하철 노선에 구간이 하나일 때 지하철역을 제외한다.")
    @Test
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> removeResponse = 노선에_지하철역_제외_요청(line, STATION_2);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }

    private ExtractableResponse<Response> 노선에_지하철역_등록_요청(LineResponse line, Long upStationId, Long downStationId, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(sectionRequest).
                when().
                post("/lines/{lineId}/sections", line.getId()).
                then().
                log().all().
                extract();
    }

    private ExtractableResponse<Response> 노선에_지하철역_제외_요청(LineResponse line, StationResponse station) {
        return RestAssured.given().log().all().
                when().
                delete("/lines/{lineId}/sections?stationId={stationId}", line.getId(), station.getId()).
                then().
                log().all().
                extract();
    }

    private void 지하철_노선에_지하철역_순서_정렬됨(LineResponse response, List<StationResponse> expectedStations) {
        List<Long> stationIds = response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철_노선에_지하철역_제외됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선에_지하철역_제외_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private LineResponse 노선_생성_요청(LineRequest lineRequest) {
        return LineSteps.노선_생성_요청(lineRequest).body().as(LineResponse.class);
    }

    private LineResponse 노선_조회_요청(ExtractableResponse<Response> mockLine) {
        return LineSteps.노선_조회_요청(mockLine).body().as(LineResponse.class);
    }

    private StationResponse 지하철역_생성_요청(String name) {
        return StationAcceptanceTest.지하철역_생성_요청(name).body().as(StationResponse.class);
    }
}
