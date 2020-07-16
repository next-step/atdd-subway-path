package nextstep.subway.line.acceptance;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.*;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선에 역 제외 관련 기능")
public class LineStationRemoveAcceptanceTest extends AcceptanceTest {
    private Long lineId;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> createLineResponse = 지하철_노선_등록되어_있음("2호선", "GREEN");
        ExtractableResponse<Response> createdStationResponse1 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse2 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse3 = 지하철역_등록되어_있음("선릉역");

        lineId = createLineResponse.as(LineResponse.class).getId();
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(lineId, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId, stationId1, stationId2);
        지하철_노선에_지하철역_등록되어_있음(lineId, stationId2, stationId3);
    }

    @DisplayName("지하철 노선에 등록된 마지막 지하철역을 제외한다.")
    @Test
    void removeLineStation1() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철역_제외_요청(lineId, stationId3);

        // then
        지하철_노선에_지하철역_제외됨(deleteResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        지하철_노선에_지하철역_제외_확인됨(response, stationId3);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(stationId1, stationId2));
    }

    @DisplayName("지하철 노선에 등록된 중간 지하철역을 제외한다.")
    @Test
    void removeLineStation2() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철역_제외_요청(lineId, stationId2);

        // then
        지하철_노선에_지하철역_제외됨(deleteResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        지하철_노선에_지하철역_제외_확인됨(response, stationId2);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(stationId1, stationId3));
    }

    @DisplayName("지하철 노선에서 등록되지 않는 역을 제외한다.")
    @Test
    void removeLineStation5() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제외_요청(lineId, 20L);

        // then
        지하철_노선에_지하철역_제외_실패됨(response);
    }
}
