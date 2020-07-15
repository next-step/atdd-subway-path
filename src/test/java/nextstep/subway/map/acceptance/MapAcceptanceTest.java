package nextstep.subway.map.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.지하철역_등록되어_있음;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class MapAcceptanceTest extends AcceptanceTest {
    private Long lineId1;
    private Long lineId2;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> createLineResponse1 = 지하철_노선_등록되어_있음("2호선", "GREEN");
        ExtractableResponse<Response> createLineResponse2 = 지하철_노선_등록되어_있음("신분당선", "RED");
        ExtractableResponse<Response> createdStationResponse1 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse2 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse3 = 지하철역_등록되어_있음("선릉역");
        ExtractableResponse<Response> createdStationResponse4 = 지하철역_등록되어_있음("양재역");

        lineId1 = createLineResponse1.as(LineResponse.class).getId();
        lineId2 = createLineResponse2.as(LineResponse.class).getId();
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();
        stationId4 = createdStationResponse4.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(lineId1, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId1, stationId2);
        지하철_노선에_지하철역_등록되어_있음(lineId1, stationId2, stationId3);
        지하철_노선에_지하철역_등록되어_있음(lineId2, null, stationId1);
        지하철_노선에_지하철역_등록되어_있음(lineId2, stationId1, stationId4);
    }

    @DisplayName("지하철 노선도를 조회한다.")
    @Test
    void loadMap() {
        // when: 지하철 노선도를 조회하는 요청을 보낸다.
        ExtractableResponse<Response> response = MapAcceptanceStep.지하철_노선도를_캐시로_요청한다();

        // then: 지하철 노선도가 응답된다.
        MapAcceptanceStep.지하철_노선도가_응답된다(response);

        // and: 지하철 노선도에 노선별 지하철역 순서가 정렬된다.
    }

    @DisplayName("캐시 적용을 검증한다.")
    @Test
    void loadMapWithETag() {
        // when: 지하철 노선도를 조회하는 요청을 보낸다.
        ExtractableResponse<Response> response = MapAcceptanceStep.지하철_노선도를_캐시로_요청한다();

        // then: 지하철 노선도가 응답된다. 이 때, eTag가 명시된다.
        MapAcceptanceStep.지하철_노선도에_eTag가_명시된다(response);

        // when: 지하철 노선도를 조회하는 요청을 보낼 때 eTag를 명시한다.
        ExtractableResponse<Response> cachedResponse = MapAcceptanceStep.지하철_노선도를_캐시로_요청한다(response.header("eTag"));

        // then: 지하철 노선도를 조회할 때 캐시가 적용된다.
        MapAcceptanceStep.지하철_노선도_응답시_캐시가_적용된다(cachedResponse);
    }
}
