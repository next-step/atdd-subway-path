package nextstep.subway.line.acceptance;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.*;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.*;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class LineStationAddAcceptanceTest extends AcceptanceTest {
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
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineId, null, stationId1);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("지하철 노선 조회 시 역 정보가 포함된다.")
    @Test
    void getLineWithStations() {
        // given
        지하철_노선에_지하철역_등록되어_있음(lineId, null, stationId1);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        지하철_노선_정보_응답됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Lists.newArrayList(stationId1));
    }

    @DisplayName("지하철 노선에 여러개의 역을 순서대로 등록한다.")
    @Test
    void addLineStationInOrder() {
        // when
        ExtractableResponse<Response> lineStationResponse = 지하철_노선에_지하철역_등록_요청(lineId, null, stationId1);
        지하철_노선에_지하철역_등록_요청(lineId, stationId1, stationId2);
        지하철_노선에_지하철역_등록_요청(lineId, stationId2, stationId3);

        // then
        assertThat(lineStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        지하철_노선_정보_응답됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Lists.newArrayList(1L, 2L, 3L));
    }

    @DisplayName("지하철 노선에 여러개의 역을 순서없이 등록한다.")
    @Test
    void addLineStationInAnyOrder() {
        // when
        ExtractableResponse<Response> lineStationResponse = 지하철_노선에_지하철역_등록_요청(lineId, null, stationId1);
        지하철_노선에_지하철역_등록_요청(lineId, stationId1, stationId2);
        지하철_노선에_지하철역_등록_요청(lineId, stationId1, stationId3);

        // then
        assertThat(lineStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        지하철_노선_정보_응답됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Lists.newArrayList(1L, 3L, 2L));
    }
}
