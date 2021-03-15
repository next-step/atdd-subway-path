package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 광교역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);

        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", 강남역.getId() + "");
        lineCreateParams.put("downStationId", 양재역.getId() + "");
        lineCreateParams.put("distance", 10 + "");
        신분당선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when역
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역));
    }

    @DisplayName("노선 출발역 앞에 새로운 역을 등록할 경우")
    @Test
    void addSectionBeforeLineUpStation() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 강남역, 7);

        // then
        지하철_노선에_지하철역_등록됨(response);
        ExtractableResponse<Response> response_stations = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response_stations);
        지하철_노선에_지하철역_순서_정렬됨(response_stations, Arrays.asList(정자역, 강남역, 양재역, 광교역));
    }

    @DisplayName("노선 종점역 뒤로 새로운 역을 등록할 경우")
    @Test
    void addSectionAfterLineDownStation() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 7);

        // then
        지하철_노선에_지하철역_등록됨(response);
        ExtractableResponse<Response> response_stations = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response_stations);
        지하철_노선에_지하철역_순서_정렬됨(response_stations, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 상행역 기준")
    @Test
    void addSectionBetweenStationsWithUpStationInfo() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 7);

        // then
        지하철_노선에_지하철역_등록됨(response);
        ExtractableResponse<Response> response_stations = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response_stations);
        지하철_노선에_지하철역_순서_정렬됨(response_stations, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 하행역 기준")
    @Test
    void addSectionBetweenStationsWithDownStationInfo() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 7);

        // then
        지하철_노선에_지하철역_등록됨(response);
        ExtractableResponse<Response> response_stations = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response_stations);
        지하철_노선에_지하철역_순서_정렬됨(response_stations, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("길이가 잘못된 역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionBetweenStationsWithWrongDistance() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 15);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    public static void 지하철_노선에_지하철역_제외됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_제외_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
