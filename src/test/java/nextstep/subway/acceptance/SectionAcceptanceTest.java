package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.model.LineEntitiesHelper.*;
import static nextstep.subway.model.SectionEntitiesHelper.*;
import static nextstep.subway.model.StationEntitiesHelper.*;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

class SectionAcceptanceTest extends AcceptanceTest {

    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 역삼역_ID;
    private Long 선릉역_ID;
    private String createdLineUri;
    private String requestUri;
    private String FIRST_SECTION_URI = "/lines/1/sections?downStationId=2";
    private static final String SECTION_URI = "/sections";
    private Map<String, Object> 역삼_TO_강남 = new HashMap<>();
    private Map<String, Object> 선릉_TO_역삼 = new HashMap<>();
    private Map<String, Object> 이호선 = new HashMap<>();

    @BeforeEach
    void setFixtures() {
        교대역_ID = 지하철역_생성_요청후_아이디_조회(교대역);
        강남역_ID = 지하철역_생성_요청후_아이디_조회(강남역);
        역삼역_ID = 지하철역_생성_요청후_아이디_조회(역삼역);
        선릉역_ID = 지하철역_생성_요청후_아이디_조회(선릉역);
        이호선 = newLine("이호선", "bg-green-600", 교대역_ID, 강남역_ID, 2);
        createdLineUri = 노선_생성_요청(이호선).header(LOCATION);
        requestUri = createdLineUri + SECTION_URI;
        역삼_TO_강남 = newSection(역삼역_ID, 강남역_ID, 3);
        선릉_TO_역삼 = newSection(선릉역_ID, 역삼역_ID, 5);
    }

    /**
     * When 구간 생성을 요청하면
     * Then 구간 생성이 성공한다
     */
    @DisplayName("구간 생성")
    @Test
    void createSection() {
        ExtractableResponse<Response> response = 구간_생성_요청(역삼_TO_강남, requestUri);
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();
    }

    /**
     * Given 구간 생성을 요청하고
     * When 현재 등록되어있지 않은 구간 생성을 요청하면
     * Then 구간 생성이 실패한다
     */
    @DisplayName("새로운 구간의 상행역은 현재 등록되어있는 하행 종점역이어야 한다")
    @Test
    void theNewSectionMustBeTheUnderTerminateStationCurrentlyRegistered() {
        구간_생성_요청(역삼_TO_강남, requestUri);

        Long 정자역_ID = 지하철역_생성_요청후_아이디_조회(정자역);
        Long 판교역_ID = 지하철역_생성_요청후_아이디_조회(판교역);
        Map<String, Object> newParams = newSection(정자역_ID, 판교역_ID, 5);
        ExtractableResponse<Response> failResponse = 구간_생성_요청(newParams, requestUri);

        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /**
     * Given 구간 생성을 요청하고
     * Given 구간 생성을 요청하고
     * When 새로운 구간의 하행역이 이전에 생성된 구간에 포함된 상태에서 구간 생성을 요청하면
     * Then 구간 생성이 실패한다.
     */
    @DisplayName("새로운 구간의 하행역은 현재 등록되어있는 역일 수 없다")
    @Test
    void theNewDownSectionCannotBeRegistered() {
        ExtractableResponse<Response> createResponse1 = 구간_생성_요청(역삼_TO_강남, requestUri);
        ExtractableResponse<Response> createResponse2 = 구간_생성_요청(선릉_TO_역삼, requestUri);

        Map<String, Object> params = newSection(선릉역_ID, 선릉역_ID, 5);
        ExtractableResponse<Response> failResponse = 구간_생성_요청(params, requestUri);

        assertThat(createResponse1.header(LOCATION)).isNotNull();
        assertThat(createResponse2.header(LOCATION)).isNotNull();
        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /**
     * Given 하나의 구간이 생성된 상태에서
     * When 생성된 구간 삭제를 요청하면
     * Then 삭제에 실패한다
     */
    @DisplayName("구간이 1개인 경우 역을 삭제할 수 없다")
    @Test
    void ifThereIsOneSectorTheStationCannotBeDeleted() {
        ExtractableResponse<Response> failResponse = 구간_삭제_요청(FIRST_SECTION_URI);
        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /**
     * Given 구간 생성을 요청하고
     * When 첫 번째에 생성된 구간 삭제를 요청하면
     * Then 삭제에 실패한다
     */
    @DisplayName("하행 종점역이 아닌 구간에 대한 삭제는 실패한다")
    @Test
    void deleteOfNonUnderTerminateSectionFails() {
        구간_생성_요청(역삼_TO_강남, requestUri);
        ExtractableResponse<Response> failResponse = 구간_삭제_요청(FIRST_SECTION_URI);
        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /**
     * Given 구간 생성을 요청하고
     * When 마지막에 생성된 구간 삭제를 요청하면
     * Then 삭제에 성공한다
     */
    @DisplayName("하행 종점역만 제거할 수 있다")
    @Test
    void onlyTheUnderTerminateStationCanBeRemoved() {
        ExtractableResponse<Response> createResponse = 구간_생성_요청(역삼_TO_강남, requestUri);
        ExtractableResponse<Response> failResponse = 구간_삭제_요청(createResponse.header(LOCATION));
        assertThat(failResponse.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    /**
     * Given 구간 생성을 요청하고
     * When 노선 조회를 요청하면
     * Then 역 목록을 포함한 노선을 응답 받는다.
     */
    @DisplayName("등록된 구간을 통해 역 목록 조회 기능")
    @Test
    void findStationsThroughRegisteredSectionsInLine() {
        구간_생성_요청(역삼_TO_강남, requestUri);
        ExtractableResponse<Response> findResponse = 노선_단건_조회_요청(createdLineUri);
        assertThat(findResponse.jsonPath().getList("stations")).isNotEmpty();
    }
}
