package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;
import nextstep.marker.AcceptanceTest;
import nextstep.subway.controller.resonse.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.AcceptanceTestUtils.*;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
class SectionAcceptanceTest extends SectionAcceptanceTestHelper {


    /**
     * 지하철노선 구간 등록
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선에 추가로 구간을 등록할때
     * 새로운 노선의 상행역이 기존 노선의 하행역이 아니면
     * Then InvalidSectionUpStationException 이 발생한다
     */
    @Test
    void 신규_구간_상행역_불일치_등록_실패() {
        //given
        ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10);
        long createdLineId = getId(lineCratedResponse);
        long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

        ValidatableResponse stationCreatedResponse = createStation("길음역");
        Long sectionDownStationId = getId(stationCreatedResponse);

        //when
        ValidatableResponse sectionCreatedResponse = createSection(createdLineId, sectionDownStationId, downStationId, 10);

        //then
        verifyResponseStatus(sectionCreatedResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지하철노선 구간 등록
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선에 추가로 구간을 등록할때
     * 새로운 노선의 하행역이 기존 노선에 등록되어 있는 역이면
     * Then InvalidSectionDownStationException 이 발생한다
     */
    @Test
    void 신규_구간_하행역_기등록_실패() {
        //given
        ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10);
        long createdLineId = getId(lineCratedResponse);
        long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);
        long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

        //when
        ValidatableResponse sectionCreatedResponse = createSection(createdLineId, downStationId, upStationId, 10);

        //then
        verifyResponseStatus(sectionCreatedResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     * /**
     * 지하철노선 구간 등록
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선에 추가로 구간을 등록할때
     * 새로운 노선의 상행역이 기존 노선의 하행역이라면
     * Then 새로운 구간이 노선에 추가되고 조회 시 하행역이 변경되고 거리가 추가 된다.
     */
    @Test
    void 신규_구간_등록_성공() {
        //given
        int distance = 10;
        ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
        long createdLineId = getId(lineCratedResponse);
        long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

        int sectionDistance = 3;
        ValidatableResponse stationCreatedResponse = createStation("길음역");
        Long sectionDownStationId = getId(stationCreatedResponse);

        //when
        ValidatableResponse sectionCreatedResponse = createSection(createdLineId, downStationId, sectionDownStationId, sectionDistance);

        //then
        verifyResponseStatus(sectionCreatedResponse, HttpStatus.CREATED);
        ValidatableResponse createdSectionResponse = getResource(getLocation(lineCratedResponse));
        verifyResponseStatus(createdSectionResponse, HttpStatus.OK);
        verifySectionAdded(createdSectionResponse, "신분당선", "bg-red-600", "강남역", "언주역", distance + sectionDistance);
    }


    /**
     * /**
     * 지하철노선 구간 제거
     * Given 지하철 노선을 생성하고 생성한 지하철 노선에 추가로 구간을 등록한뒤
     * When 중간에 있는 역을 제거하려 하면
     * Then 예외가 발생하고 실패한다.
     */
    @Test
    void 지하철노선_구간_제거시_하행_종점역이_아니면_실패() {
        //given
        ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10);
        long createdLineId = getId(lineCratedResponse);
        long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

        ValidatableResponse stationCreatedResponse = createStation("길음역");
        Long sectionDownStationId = getId(stationCreatedResponse);
        createSection(createdLineId, downStationId, sectionDownStationId, 3);

        //when
        ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + downStationId);

        //then
        verifyResponseStatus(sectionDeletedResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * /**
     * 지하철노선 구간 제거
     * Given 지하철 노선을 생성하고
     * When 하행역을 제거하려 하면
     * Then 예외가 발생하고 실패한다.
     */
    @Test
    void 지하철노선_구간_제거시_구간이_한개인_경우_실패() {
        //given
        ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10);
        Long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

        //when
        ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + downStationId);

        //then
        verifyResponseStatus(sectionDeletedResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * /**
     * 지하철노선 구간 제거
     * Given 지하철 노선을 생성하고 생성한 지하철 노선에 추가로 구간을 등록한뒤
     * When 하행 종점역을 제거한뒤
     * Then 다시 조회하면 제거된 역을 제외한 상행역과 하행역이 조회되고 거리도 줄어든다
     */
    @Test
    void 지하철노선_구간_제거_성공() {
        //given
        int distance = 10;
        ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
        long createdLineId = getId(lineCratedResponse);
        long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

        ValidatableResponse stationCreatedResponse = createStation("길음역");
        Long sectionDownStationId = getLong(stationCreatedResponse, DOWN_STATION_ID_JSON_PATH);
        StationResponse stationResponse = stationCreatedResponse.extract().as(StationResponse.class);
        int sectionDistance = 3;

        ValidatableResponse createdSectionResponse = createSection(createdLineId, downStationId, sectionDownStationId, sectionDistance);
        verifySectionAdded(createdSectionResponse, "신분당선", "bg-red-600", "강남역", "길음역", sectionDistance);

        //when
        ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + stationResponse.getId());

        //then
        verifyResponseStatus(sectionDeletedResponse, HttpStatus.NO_CONTENT);

        ValidatableResponse foundLineResponse = getResource(LINES_RESOURCE_URL + "/" + createdLineId);
        verifyResponseStatus(foundLineResponse, HttpStatus.OK);

        verifyFoundLine(foundLineResponse, "신분당선", "bg-red-600", "강남역", "길음역");

    }
}