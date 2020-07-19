package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.path.acceptance.step.PathAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;

@DisplayName("지하철 경로 검색 기능")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long lineId1;
    private Long lineId2;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long stationId5;

    @BeforeEach
    public void setUo() {
        super.setUp();

        ExtractableResponse<Response> createLineResponse1 = 지하철_노선_등록되어_있음("2호선", "GREEN");
        ExtractableResponse<Response> createLineResponse2 = 지하철_노선_등록되어_있음("신분당선", "RED");
        ExtractableResponse<Response> createdStationResponse1 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse2 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse3 = 지하철역_등록되어_있음("선릉역");
        ExtractableResponse<Response> createdStationResponse4 = 지하철역_등록되어_있음("양재역");
        ExtractableResponse<Response> createdStationResponse5 = 지하철역_등록되어_있음("양재시민의숲역");

        lineId1 = createLineResponse1.as(LineResponse.class).getId();
        lineId2 = createLineResponse2.as(LineResponse.class).getId();
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();
        stationId4 = createdStationResponse4.as(StationResponse.class).getId();
        stationId5 = createdStationResponse5.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(lineId1, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId1, stationId2);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId2, stationId3);
        지하철_노선에_지하철역_등록되어_있음(lineId2, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId1, stationId4);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId4, stationId5);
    }

    @DisplayName("같은 노선의 지하철역 최단 거리 경로를 검색한다.")
    @Test
    void findShortestPathOnTheSameLine() {
        // when
        ExtractableResponse<Response> pathResponse = 최단_거리_경로_조회_요청(stationId1, stationId3);

        // then
        최단_거리_경로_조회_응답됨(pathResponse);
        최단_거리_경로_조회됨(pathResponse, Lists.newArrayList(stationId1, stationId2, stationId3));
        총_거리와_소요_시간을_함께_응답함(pathResponse);
    }

    @DisplayName("다른 노선의 지하철역 최단 거리 경로를 검색한다.")
    @Test
    void findShortestPathOnTheDifferentLines() {
        // when
        ExtractableResponse<Response> pathResponse = 최단_거리_경로_조회_요청(stationId3, stationId5);

        // then
        최단_거리_경로_조회_응답됨(pathResponse);
        최단_거리_경로_조회됨(pathResponse, Lists.newArrayList(stationId3, stationId2, stationId1, stationId4, stationId5));
        총_거리와_소요_시간을_함께_응답함(pathResponse);
    }

    @DisplayName("같은 노선의 지하철역 최소 시간 경로를 검색한다.")
    @Test
    void findFastestPathOnTheSameLine() {
        // when
        ExtractableResponse<Response> pathResponse = 최소_시간_경로_조회_요청(stationId1, stationId3);

        // then
        최소_시간_경로_조회_응답됨(pathResponse);
        최소_시간_경로_조회됨(pathResponse, Lists.newArrayList(stationId1, stationId2, stationId3));
        총_거리와_소요_시간을_함께_응답함(pathResponse);
    }

    @DisplayName("다른 노선의 지하철역 최단 거리 경로를 검색한다.")
    @Test
    void findFastestPathOnTheDifferentLines() {
        // when
        ExtractableResponse<Response> pathResponse = 최소_시간_경로_조회_요청(stationId3, stationId5);

        // then
        최소_시간_경로_조회_응답됨(pathResponse);
        최소_시간_경로_조회됨(pathResponse, Lists.newArrayList(stationId3, stationId2, stationId1, stationId4, stationId5));
        총_거리와_소요_시간을_함께_응답함(pathResponse);
    }
}
