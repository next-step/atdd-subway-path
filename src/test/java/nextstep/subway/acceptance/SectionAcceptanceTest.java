package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;
import nextstep.marker.AcceptanceTest;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.AcceptanceTestUtils.*;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
class SectionAcceptanceTest extends SectionAcceptanceTestHelper {


    @Nested
    class 구간_등록_성공 {

        /**
         * 지하철노선 구간 등록
         * Given 지하철 노선을 생성하고
         * When 생성한 지하철 노선에 추가로 구간을 등록할때
         * 새로운 노선의 상행역이 기존 노선의 상행역이라면
         * Then 새로운 구간이 노선에 추가되고 조회 시 하행역이 변경된다.
         */
        @Test
        void 기존_노선_상행역에_신규_구간_상행역_등록_성공() {
            //given
            long distance = 10L;
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
            long createdLineId = getId(lineCratedResponse);
            long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);

            long newSectionDistance = 3L;
            ValidatableResponse stationCreatedResponse = createStation("길음역");
            Long newStationId = getId(stationCreatedResponse);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, upStationId, newStationId, newSectionDistance);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.OK);

            ValidatableResponse createdSectionResponse = getResource(getLocation(lineCratedResponse));
            verifyResponseStatus(createdSectionResponse, HttpStatus.OK);

            LineResponse lineResponse = createdSectionResponse.extract().as(LineResponse.class);
            verifyLineResponse(lineResponse, "신분당선", "bg-red-600", distance);
            verifyLineResponseStationNames(lineResponse, "강남역", "길음역", "언주역");
        }

        /**
         * 지하철노선 구간 등록
         * Given 지하철 노선을 생성하고
         * When 생성한 지하철 노선에 추가로 구간을 등록할때
         * 새로운 노선의 하행역이 기존 노선의 상행역이라면
         * Then 새로운 구간이 노선에 추가되고 조회 시 상행역이 변경되고 거리가 추가 된다.
         */
        @Test
        void 기존_노선_상행역에_신규_구간_하행역_등록_성공() {
            //given
            long distance = 10L;
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
            long createdLineId = getId(lineCratedResponse);
            long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);

            long newSectionDistance = 3L;
            ValidatableResponse stationCreatedResponse = createStation("길음역");
            Long newStationId = getId(stationCreatedResponse);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, newStationId, upStationId, newSectionDistance);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.OK);

            ValidatableResponse createdSectionResponse = getResource(getLocation(lineCratedResponse));
            verifyResponseStatus(createdSectionResponse, HttpStatus.OK);

            LineResponse lineResponse = createdSectionResponse.extract().as(LineResponse.class);
            verifyLineResponse(lineResponse, "신분당선", "bg-red-600", distance + newSectionDistance);
            verifyLineResponseStationNames(lineResponse, "길음역", "강남역", "언주역");
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
        void 기존_노선_하행역에_신규_구간_상행역_등록_성공() {
            //given
            long distance = 10L;
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
            long createdLineId = getId(lineCratedResponse);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            long newSectionDistance = 3L;
            ValidatableResponse stationCreatedResponse = createStation("길음역");
            Long newStationId = getId(stationCreatedResponse);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, downStationId, newStationId, newSectionDistance);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.OK);
            ValidatableResponse createdSectionResponse = getResource(getLocation(lineCratedResponse));
            verifyResponseStatus(createdSectionResponse, HttpStatus.OK);
        }
    }

    @Nested
    class 구간_등록_실패 {

        /**
         * 지하철노선 구간 등록
         * Given 지하철 노선을 생성하고
         * When 생성한 지하철 노선에 추가로 구간을 등록할때
         * 새로운 노선의 하행역이 기존 노선에 등록되어 있는 역이면
         * Then 구간 등록에 실패한다
         */
        @Test
        void 신규_구간_하행역_기등록_실패() {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            long createdLineId = getId(lineCratedResponse);
            long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, downStationId, upStationId, 10L);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * 지하철노선 구간 등록
         * Given 지하철 노선을 생성하고
         * When 생성한 지하철 노선에 추가로 구간을 등록할때
         * 새로운 노선이 추가되는 구간의 길이가 기존 역 사이보다 크거나 같으면
         * Then 구간 등록에 실패한다
         */
        @CsvSource(value = {"10", "11", "12"})
        @ParameterizedTest
        void 신규_구간_길이가_기존과_동일하거나_더_크면_실패(long newSectionDistance) {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            long createdLineId = getId(lineCratedResponse);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            ValidatableResponse stationCreatedResponse = createStation("길음역");
            Long newStationId = getId(stationCreatedResponse);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, downStationId, newStationId, newSectionDistance);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * 지하철노선 구간 등록
         * Given 지하철 노선을 생성하고
         * When 생성한 지하철 노선에 추가로 구간을 등록할때
         * 새로운 노선의 상행, 하행역이 모두 이미 구간 등록이 되어 있는 역인 경우
         * Then 구간 등록에 실패한다
         */
        @Test
        void 신규_구간_기등록_실패() {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            long createdLineId = getId(lineCratedResponse);
            long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, upStationId, downStationId, 2L);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * 지하철노선 구간 등록
         * Given 지하철 노선을 생성하고
         * When 생성한 지하철 노선에 추가로 구간을 등록할때
         * 새로운 노선의 상행, 하행역 중 어느 것도 노선에 등록 된 역이 아니면
         * Then 구간 등록에 실패한다
         */
        @Test
        void 신규_구간_노선_구간_상행_하행_모두_미등록_실패() {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            ValidatableResponse firstNewStationCreatedResponse = createStation("부천역");
            Long firstNewStationId = getId(firstNewStationCreatedResponse);

            ValidatableResponse secondNewStationCreatedResponse = createStation("송내역");
            Long secondNewStationId = getId(secondNewStationCreatedResponse);
            long createdLineId = getId(lineCratedResponse);

            //when
            ValidatableResponse sectionCreatedResponse = createSection(createdLineId, firstNewStationId, secondNewStationId, 2L);

            //then
            verifyResponseStatus(sectionCreatedResponse, HttpStatus.BAD_REQUEST);
        }


    }

    @Nested
    class 구간_제거_성공 {

        /**
         * 지하철노선 구간 제거
         * Given 지하철 노선을 생성하고 생성한 지하철 노선에 추가로 구간을 등록한뒤
         * When 상행 종점역을 제거한뒤
         * Then 다시 조회하면 제거된 역을 제외한 상행역과 하행역이 조회되고 거리도 줄어든다
         */
        @Test
        void 상행역_구간_제거_성공() {
            //given
            long distance = 10L;
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
            long createdLineId = getId(lineCratedResponse);
            long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            ValidatableResponse stationCreatedResponse = createStation("길음역");
            StationResponse stationResponse = stationCreatedResponse.extract().as(StationResponse.class);
            Long newDownStationId = stationResponse.getId();
            long newSectionDistance = 3L;

            createSection(createdLineId, downStationId, newDownStationId, newSectionDistance);

            //when
            ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + upStationId);

            //then
            verifyResponseStatus(sectionDeletedResponse, HttpStatus.NO_CONTENT);

            ValidatableResponse foundLineResponse = getResource(LINES_RESOURCE_URL + "/" + createdLineId);
            verifyResponseStatus(foundLineResponse, HttpStatus.OK);

            verifyFoundLine(foundLineResponse, "신분당선", "bg-red-600", newSectionDistance, "언주역", "길음역");
        }

        /**
         * 지하철노선 구간 제거
         * Given 지하철 노선을 생성하고 생성한 지하철 노선에 추가로 구간을 등록한뒤
         * When 하행 종점역을 제거한뒤
         * Then 다시 조회하면 제거된 역을 제외한 상행역과 하행역이 조회되고 거리도 줄어든다
         */
        @Test
        void 하행역_구간_제거_성공() {
            //given
            long distance = 10L;
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
            long createdLineId = getId(lineCratedResponse);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            ValidatableResponse stationCreatedResponse = createStation("길음역");
            StationResponse stationResponse = stationCreatedResponse.extract().as(StationResponse.class);
            Long newDownStationId = stationResponse.getId();
            long newSectionDistance = 3L;

            createSection(createdLineId, downStationId, newDownStationId, newSectionDistance);

            //when
            ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + newDownStationId);

            //then
            verifyResponseStatus(sectionDeletedResponse, HttpStatus.NO_CONTENT);

            ValidatableResponse foundLineResponse = getResource(LINES_RESOURCE_URL + "/" + createdLineId);
            verifyResponseStatus(foundLineResponse, HttpStatus.OK);

            verifyFoundLine(foundLineResponse, "신분당선", "bg-red-600", distance, "강남역", "언주역");
        }

        /**
         * 지하철노선 구간 제거
         * Given 지하철 노선을 생성하고 생성한 지하철 노선에 추가로 구간을 등록한뒤
         * When 중간 역을 제거한뒤
         * Then 다시 조회하면 제거된 역을 제외한 상행역과 하행역이 조회 된다
         */
        @Test
        void 중간역_구간_제거_성공() {
            //given
            long distance = 10L;
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", distance);
            long createdLineId = getId(lineCratedResponse);
            long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            ValidatableResponse stationCreatedResponse = createStation("길음역");
            StationResponse stationResponse = stationCreatedResponse.extract().as(StationResponse.class);
            Long newDownStationId = stationResponse.getId();
            long newSectionDistance = 3L;

            createSection(createdLineId, downStationId, newDownStationId, newSectionDistance);

            //when
            ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + downStationId);

            //then
            verifyResponseStatus(sectionDeletedResponse, HttpStatus.NO_CONTENT);

            ValidatableResponse foundLineResponse = getResource(LINES_RESOURCE_URL + "/" + createdLineId);
            verifyResponseStatus(foundLineResponse, HttpStatus.OK);

            verifyFoundLine(foundLineResponse, "신분당선", "bg-red-600", distance + newSectionDistance, "강남역", "길음역");
        }


    }

    @Nested
    class 구간_제거_실패 {

        /**
         * 지하철노선 구간 제거
         * Given 지하철 노선을 생성하고
         * When 상행역을 제거하려 하면
         * Then 예외가 발생하고 실패한다.
         */
        @Test
        void 지하철노선_구간_제거시_구간이_한개인_경우_상행역_제거_실패() {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            Long upStationId = getLong(lineCratedResponse, UP_STATION_ID_JSON_PATH);

            //when
            ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + upStationId);

            //then
            verifyResponseStatus(sectionDeletedResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * 지하철노선 구간 제거
         * Given 지하철 노선을 생성하고
         * When 하행역을 제거하려 하면
         * Then 예외가 발생하고 실패한다.
         */
        @Test
        void 지하철노선_구간_제거시_구간이_한개인_경우_하행역_제거_실패() {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            Long downStationId = getLong(lineCratedResponse, DOWN_STATION_ID_JSON_PATH);

            //when
            ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + downStationId);

            //then
            verifyResponseStatus(sectionDeletedResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * 지하철노선 구간 제거
         * Given 지하철 노선을 생성하고
         * When 노선에 없는 역을 제거하려 하면
         * Then 예외가 발생하고 실패한다.
         */
        @Test
        void 지하철노선_구간_제거시_구간에_없는_역인_경우_실패() {
            //given
            ValidatableResponse lineCratedResponse = createLines("신분당선", "bg-red-600", "강남역", "언주역", 10L);
            Long notInSectionStationId = getId(createStation("성수역"));

            //when
            ValidatableResponse sectionDeletedResponse = deleteResource(getLocation(lineCratedResponse) + SECTION_RESOURCE_URL + "?stationId=" + notInSectionStationId);

            //then
            verifyResponseStatus(sectionDeletedResponse, HttpStatus.BAD_REQUEST);
        }


    }
}