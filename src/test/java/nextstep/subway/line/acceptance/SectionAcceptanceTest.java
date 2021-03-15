package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.acceptance.LineSectionAcceptanceTest.지하철_노선에_지하철역_등록됨;
import static nextstep.subway.line.acceptance.LineSectionAcceptanceTest.지하철_노선에_지하철역_순서_정렬됨;
import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;

@DisplayName("지하철 노선에 구간 등록하기 위한 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    private LineResponse 이호선;
    private StationResponse 을지로4가역;
    private StationResponse 을지로3가역;
    private StationResponse 을지로입구역;
    private StationResponse 시청역;
    private StationResponse 충정로역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        을지로4가역 = 지하철역_등록되어_있음("을지로4가역").as(StationResponse.class);
        을지로3가역 = 지하철역_등록되어_있음("을지로3가역").as(StationResponse.class);
        을지로입구역 = 지하철역_등록되어_있음("을지로입구역").as(StationResponse.class);
        시청역 = 지하철역_등록되어_있음("시청역").as(StationResponse.class);
        충정로역 = 지하철역_등록되어_있음("충정로역").as(StationResponse.class);

        Map<String, Object> lineRequestParam = createLineParams("이호선", "green", 을지로3가역.getId(), 시청역.getId(), 10);
        이호선 = 지하철_노선_등록되어_있음(lineRequestParam).as(LineResponse.class);
    }

    @DisplayName("지하철 구간 등록 시 역 사이에 새로운 구간을 추가")
    @Test
    void addSectionWithUpStation() {
        //when
        //지하철 구간 등록
        지하철_노선에_지하철역_등록_요청(이호선, 을지로3가역, 을지로입구역, 3);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(을지로3가역, 을지로입구역, 시청역));
    }

    @DisplayName("지하철 구간 등록 시 새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionWithUpStationAsDownStation() {
        //when
        //지하철 구간 등록
        지하철_노선에_지하철역_등록_요청(이호선, 을지로4가역, 을지로3가역, 5);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(을지로3가역, 을지로입구역, 시청역));
    }

    @DisplayName("지하철 구간 등록 시 새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionWithDownStationAsUpStation() {
        //when
        //지하철 구간 등록
        지하철_노선에_지하철역_등록_요청(이호선, 시청역, 충정로역, 2);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(을지로3가역, 을지로입구역, 시청역));
    }

    private Map<String, Object> createLineParams(String lineName, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }
}
