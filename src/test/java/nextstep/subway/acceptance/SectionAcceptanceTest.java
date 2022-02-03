package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.model.LineEntitiesHelper.newLine;
import static nextstep.subway.model.LineEntitiesHelper.노선_단건_조회_요청;
import static nextstep.subway.model.LineEntitiesHelper.노선_생성_요청;
import static nextstep.subway.model.SectionEntitiesHelper.newSection;
import static nextstep.subway.model.SectionEntitiesHelper.구간_삭제_요청;
import static nextstep.subway.model.SectionEntitiesHelper.구간_생성_요청;
import static nextstep.subway.model.StationEntitiesHelper.강남역;
import static nextstep.subway.model.StationEntitiesHelper.교대역;
import static nextstep.subway.model.StationEntitiesHelper.삼성역;
import static nextstep.subway.model.StationEntitiesHelper.서초역;
import static nextstep.subway.model.StationEntitiesHelper.선릉역;
import static nextstep.subway.model.StationEntitiesHelper.역삼역;
import static nextstep.subway.model.StationEntitiesHelper.정자역;
import static nextstep.subway.model.StationEntitiesHelper.지하철역_생성_요청후_아이디_조회;
import static nextstep.subway.model.StationEntitiesHelper.판교역;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

class SectionAcceptanceTest extends AcceptanceTest {

    private Long 서초역_ID;
    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 역삼역_ID;
    private Long 선릉역_ID;
    private Long 삼성역_ID;
    private String createdLineUri;
    private String requestUri;
    private String FIRST_LINE_URI = "/lines/1";
    private String FIRST_SECTION_URI = "/lines/1/sections?stationId=2";
    private static final String SECTION_URI = "/sections";
    private Map<String, Object> 서초_TO_교대 = new HashMap<>();
    private Map<String, Object> 교대_TO_강남 = new HashMap<>();
    private Map<String, Object> 강남_TO_역삼 = new HashMap<>();
    private Map<String, Object> 역삼_TO_선릉 = new HashMap<>();
    private Map<String, Object> 선릉_TO_삼성 = new HashMap<>();
    private Map<String, Object> 이호선 = new HashMap<>();

    @BeforeEach
    void setFixtures() {
        서초역_ID = 지하철역_생성_요청후_아이디_조회(서초역);
        교대역_ID = 지하철역_생성_요청후_아이디_조회(교대역);
        강남역_ID = 지하철역_생성_요청후_아이디_조회(강남역);
        역삼역_ID = 지하철역_생성_요청후_아이디_조회(역삼역);
        선릉역_ID = 지하철역_생성_요청후_아이디_조회(선릉역);
        삼성역_ID = 지하철역_생성_요청후_아이디_조회(삼성역);

        이호선 = newLine("이호선", "bg-green-600", 교대역_ID, 역삼역_ID, 8);
        createdLineUri = 노선_생성_요청(이호선).header(LOCATION);
        requestUri = createdLineUri + SECTION_URI;

        서초_TO_교대 = newSection(서초역_ID, 교대역_ID, 3);
        교대_TO_강남 = newSection(교대역_ID, 강남역_ID, 3);
        강남_TO_역삼 = newSection(강남역_ID, 역삼역_ID, 3);
        역삼_TO_선릉 = newSection(역삼역_ID, 선릉역_ID, 5);
        선릉_TO_삼성 = newSection(선릉역_ID, 삼성역_ID, 5);
    }

    /**
     * When 구간 생성을 요청하면
     * Then 구간 생성이 성공한다
     */
    @DisplayName("구간 생성")
    @Test
    void createSection() {
        ExtractableResponse<Response> response = 구간_생성_요청(역삼_TO_선릉, requestUri);
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
        구간_생성_요청(역삼_TO_선릉, requestUri);

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
        ExtractableResponse<Response> createResponse1 = 구간_생성_요청(역삼_TO_선릉, requestUri);
        ExtractableResponse<Response> createResponse2 = 구간_생성_요청(선릉_TO_삼성, requestUri);

        Map<String, Object> params = newSection(삼성역_ID, 삼성역_ID, 5);
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
     * Then 삭제에 성공한다.
     */
    @DisplayName("구간이 2개이상 등록된 경우 위치에 상관없이 삭제가 가능하다")
    @Test
    void ifMoreThanOneSectionIsRegisteredItCanBeDeletedRegardlessOfLocation() {
        구간_생성_요청(역삼_TO_선릉, requestUri);
        ExtractableResponse<Response> response = 구간_삭제_요청(FIRST_SECTION_URI);
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    /**
     * Given 구간 생성을 요청하고
     * When 생성되지 않은 구간에 대한 삭제를 요청하면
     * Then 삭제에 실패한다.
     */
    @DisplayName("생성되지 않은 구간에 대한 삭제를 요청하면 삭제에 실패한다")
    @Test
    void ifYouRequestDeletionForAnUnCreatedSectionItCanNotBeDelete() {
        구간_생성_요청(역삼_TO_선릉, requestUri);
        String uri = "/lines/1/sections?stationId=" + Long.MAX_VALUE;
        ExtractableResponse<Response> response = 구간_삭제_요청(uri);
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /**
     * Given 구간 생성을 요청하고
     * Given 생성된 구간의 중간역에 대한 삭제를 요청하고
     * When 노선을 조회하면
     * Then 첫 역과 마지막 역이 연결된 하나의 구간을 응답 받는다.
     */
    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 된다")
    @Test
    void ifTheEndpointIsRemovedTheNextStationToComeWillBeTheEndPoint() {
        구간_생성_요청(역삼_TO_선릉, requestUri);
        구간_삭제_요청(FIRST_SECTION_URI);
        ExtractableResponse<Response> findResponse = 노선_단건_조회_요청(FIRST_LINE_URI);
        assertThat(findResponse.jsonPath().getList("stations").size()).isEqualTo(2);
    }

    /**
     * Given 구간 생성을 요청하고
     * When 노선 조회를 요청하면
     * Then 역 목록을 포함한 노선을 응답 받는다.
     */
    @DisplayName("등록된 구간을 통해 역 목록 조회 기능")
    @Test
    void findStationsThroughRegisteredSectionsInLine() {
        구간_생성_요청(역삼_TO_선릉, requestUri);
        ExtractableResponse<Response> findResponse = 노선_단건_조회_요청(createdLineUri);
        assertThat(findResponse.jsonPath().getList("stations")).isNotEmpty();
    }

    /**
     * Given 하나의 노선과 구간이 생성된 상태에서
     * When 기존 구간의 역을 기준으로 새로운 구간을 추가하면
     * Then 새로운 구간이 생성된다
     */
    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void registerNewStationBetweenStations() {
        ExtractableResponse<Response> response = 구간_생성_요청(교대_TO_강남, requestUri);
        assertThat(response.header(LOCATION)).isNotNull();
    }

    /**
     * Given 하나의 노선과 구간이 생성된 상태에서
     * When 새로운 역을 상행 종점역으로 하는 새로운 구간을 추가하면
     * Then 새로운 구간이 생성된다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void registerNewTerminateUpStation() {
        ExtractableResponse<Response> response = 구간_생성_요청(서초_TO_교대, requestUri);
        assertThat(response.header(LOCATION)).isNotNull();
    }

    /**
     * Given 하나의 노선과 구간이 생성된 상태에서
     * When 새로운 역을 하행 종점역으로 하는 새로운 구간을 추가하면
     * Then 새로운 구간이 생성된다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void registerNewTerminateDownStation() {
        ExtractableResponse<Response> response = 구간_생성_요청(역삼_TO_선릉, requestUri);
        assertThat(response.header(LOCATION)).isNotNull();
    }
}
