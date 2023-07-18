package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;
import nextstep.marker.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.AcceptanceTestUtils.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
class StationAcceptanceTest extends StationAcceptanceTestHelper {

    private static final String RESOURCE_URL = "/stations";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    void 지하철역을_생성한다() {
        // given
        String stationName = "강남역";

        // when
        ValidatableResponse stationCreatedResponse = createStation(stationName);

        // then
        verifyResponseStatus(stationCreatedResponse, HttpStatus.CREATED);

        ValidatableResponse stationFoundResponse = getResource(getLocation(stationCreatedResponse));
        verifyResponseStatus(stationFoundResponse, HttpStatus.OK);

        stationFoundResponse
                .body("name", equalTo(stationName));
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @Test
    void 지하철역을_전체_조회한다() {
        // given
        String firstStationName = "언주역";
        createStation(firstStationName);

        String secondStationName = "삼성역";
        createStation(secondStationName);

        // when
        ValidatableResponse foundStation = getResource(RESOURCE_URL);

        // then
        verifyResponseStatus(foundStation, HttpStatus.OK);

        foundStation
                .body("", hasSize(2))
                .body("[0].name", equalTo(firstStationName))
                .body("[1].name", equalTo(secondStationName));
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    void 지하철역을_삭제한다() {
        // given
        String stationName = "청담역";
        ValidatableResponse stationCreatedResponse = createStation(stationName);
        verifyResponseStatus(stationCreatedResponse, HttpStatus.CREATED);
        String createdResourceLocation = getLocation(stationCreatedResponse);

        // when
        ValidatableResponse stationDeletedResponse = deleteResource(createdResourceLocation);

        // then
        verifyResponseStatus(stationDeletedResponse, HttpStatus.NO_CONTENT);

        ValidatableResponse foundStation = getResource(createdResourceLocation);
        verifyResponseStatus(foundStation, HttpStatus.NOT_FOUND);
    }
}