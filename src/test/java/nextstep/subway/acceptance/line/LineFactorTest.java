package nextstep.subway.acceptance.line;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static nextstep.subway.acceptance.line.LineRequester.*;
import static nextstep.subway.acceptance.station.StationRequester.createStationThenReturnId;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineFactorTest {

    private static final String GANGNAM_STATION_NAME = "강남역";
    private static final String SEOLLEUNG_STATION_NAME = "선릉역";
    private static final String SUWON_STATION_NAME = "수원역";
    private static final String NOWON_STATION_NAME = "노원역";
    private static final String DEARIM_STATION_NAME = "대림역";

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";
    private static final String BUNDANG_LINE_NAME = "분당선";
    private static final String BUNDANG_LINE_COLOR = "bg-green-600";

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void 지하철노선생성() {
        // when
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // then
        지하철노선생성_역이름_검증(id);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선목록을 조회시 등록된 전체 지하철 노선이 조회되어야 한다.")
    @Test
    void 지하철노선목록조회() {
        // given
        Long shinbundangLineId = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);
        Long bundangLineId = 지하철노선_생성_후_식별값_리턴(BUNDANG_LINE_NAME, BUNDANG_LINE_COLOR, SEOLLEUNG_STATION_NAME, SUWON_STATION_NAME, 5);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회();

        // then
        지하철노선_목록_조회_식별값_역이름_검증(response, shinbundangLineId, bundangLineId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회시 등록된 지하철 노선정보가 조회되어야 한다.")
    @Test
    void 지하철노선조회() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_조회(id);

        // then
        지하철노선_조회_식별값_역이름_검증(response, id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정시 등록된 지하철 노선정보가 수정되어야 한다.")
    @Test
    void 지하철노선수정() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        지하철노선_수정(id, BUNDANG_LINE_NAME, BUNDANG_LINE_COLOR);

        // then
        지하철노선_수정_이름_색_검증(id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제시 등록된 지하철 노선정보가 삭제되어야 한다.")
    @Test
    void 지하철노선삭제() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        지하철노선_삭제(id);

        // then
        지하철노선_삭제_응답값_검증(id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 구간을 추가하면
     * Then 해당 지하철 노선 정보에 구간이 추가된다.
     */
    @DisplayName("지하철 노선 추가 후 구간 추가시 노선정보에 추가되어야 한다.")
    @Test
    void 지하철노선_구간추가_정상() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        지하철노선_구간_추가(id, SEOLLEUNG_STATION_NAME, NOWON_STATION_NAME, 3);

        // then
        지하철노선_구간_추가_결과_역이름_검증(id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역, 하행역이 노선에 모두 존재하는 구간 추가시
     * Then 해당 지하철 노선 정보에 구간이 추가가 실패된다.
     */
    @DisplayName("지하철 노선 추가 후 상행역, 하행역이 모두 존재하는 구간 추가시 실패되어야 한다.")
    @Test
    void 지하철노선_구간추가_상행역_하행역이_노선에_모두존재시_추가실패() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(id, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 3);

        // then
        지하철노선_구간추가_상행역_하행역이_노선에_모두존재시_추가실패_응답값_검증(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역, 하행역이 노선에 모두 존재하지 않는 구간 추가시
     * Then 해당 지하철 노선 정보에 구간이 추가가 실패된다.
     */
    @DisplayName("지하철 노선 추가 후 상행역, 하행역이 모두 존재하지 않는 구간 추가시 실패되어야 한다.")
    @Test
    void 지하철노선_구간추가_상행역_하행역이_노선에_모두미존재시_추가실패() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(id, NOWON_STATION_NAME, DEARIM_STATION_NAME, 3);

        // then
        지하철노선_구간추가_상행역_하행역이_노선에_모두미존재시_추가실패_응답값_검증(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존 구간에 길이를 초과하는 구간 추가시
     * Then 해당 지하철 노선 정보에 구간이 추가가 실패된다.
     */
    @DisplayName("지하철 노선 추가 후 기존 구간에 길이를 초과하는 구간 추가시 실패되어야 한다.")
    @Test
    void 지하철노선_구간추가_기존구간길이_초과시_추가실패() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_구간_추가(id, NOWON_STATION_NAME, SEOLLEUNG_STATION_NAME, 15);

        // then
        지하철노선_구간추가_기존구간길이_초과시_추가실패_응답값_검증(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 구간을 삭제하면
     * Then 해당 지하철 노선 정보에 구간이 삭제된다.
     */
    @DisplayName("지하철 노선 추가 후 구간 삭제시 노선정보에 삭제되어야 한다.")
    @Test
    void 지하철노선_구간_삭제_정상() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);
        지하철노선_구간_추가(id, SEOLLEUNG_STATION_NAME, NOWON_STATION_NAME, 3);

        // when
        지하철노선_구간_삭제(id, 지하철노선_하행종점역_식별값_조회(id));

        // then
        지하철노선_구간_삭제_정상_역이름_검증(id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 구간을 삭제하면
     * Then 구간정보가 1개이므로 삭제가 실패된다.
     */
    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간정보가 1개이므로 삭제가 실패되어야 한다.")
    @Test
    void 지하철노선_구간_삭제_최소구간일경우_삭제실패() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_구간_삭제(id, 지하철노선_하행종점역_식별값_조회(id));

        // then
        지하철노선_구간_삭제_최소구간일경우_삭제실패_응답값_검증(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선에 존재하지 않는 역을 삭제 시도하면
     * Then 삭제가 실패된다.
     */
    @DisplayName("지하철 노선 추가 후 구간 삭제시 노선에 존재하는 역이 아닐경우 삭제가 실패되어야 한다.")
    @Test
    void 지하철노선_구간_삭제_노선에존재하지않는_역_삭제실패() {
        // given
        Long id = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, 10);
        지하철노선_구간_추가(id, SEOLLEUNG_STATION_NAME, NOWON_STATION_NAME, 3);
        지하철노선_구간_추가(id, NOWON_STATION_NAME, DEARIM_STATION_NAME, 5);

        // when
        ExtractableResponse<Response> response = 지하철노선_구간_삭제(id, 지하철역_추가_식별값_리턴(SUWON_STATION_NAME));

        // then
        지하철노선_구간_삭제_노선에존재하지않는_역_삭제실패_응답값_검증(response);
    }


    private Long 지하철노선_생성_후_식별값_리턴(String name, String color, String upStationName, String downStationName, int distance) {
        return createLineThenReturnId(name, color, upStationName, downStationName, distance);
    }

    private void 지하철노선생성_역이름_검증(Long id) {
        JsonPath jsonPath = findLine(id).jsonPath();
        assertThat(jsonPath.getObject("name", String.class)).isEqualTo(SHINBUNDANG_LINE_NAME);
        assertThat(jsonPath.getList("stations.name", String.class)).containsExactly(GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME);
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회() {
        return findLines();
    }

    private void 지하철노선_목록_조회_식별값_역이름_검증(ExtractableResponse<Response> response, Long shinbundangLineId, Long bundangLineId) {
        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getList("id", Long.class)).containsExactly(shinbundangLineId, bundangLineId);
        assertThat(jsonPath.getList("stations.name")).contains(List.of(GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME), List.of(SEOLLEUNG_STATION_NAME, SUWON_STATION_NAME));
    }

    private ExtractableResponse<Response> 지하철노선_조회(Long id) {
        return findLine(id);
    }

    private void 지하철노선_조회_식별값_역이름_검증(ExtractableResponse<Response> response, Long id) {
        JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getObject("id", Long.class)).isEqualTo(id);
        assertThat(jsonPath.getList("stations.name")).contains(GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME);
    }

    private void 지하철노선_수정(Long id, String name, String color) {
        modifyLine(id, name, color);
    }

    private void 지하철노선_수정_이름_색_검증(Long id) {
        JsonPath jsonPath = findLine(id).jsonPath();
        assertThat(jsonPath.getObject("name", String.class)).isEqualTo(BUNDANG_LINE_NAME);
        assertThat(jsonPath.getObject("color", String.class)).isEqualTo(BUNDANG_LINE_COLOR);
    }

    private void 지하철노선_삭제(Long id) {
        deleteLine(id);
    }

    private void 지하철노선_삭제_응답값_검증(Long id) {
        assertThat(findLine(id).response().statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Long 지하철역_추가_식별값_리턴(String stationName) {
        return createStationThenReturnId(stationName);
    }

    private ExtractableResponse<Response> 지하철노선_구간_추가(Long lineId, String upStationName, String downStationName, int distance) {
        Long upStationId = createStationThenReturnId(upStationName);
        Long downStationId = createStationThenReturnId(downStationName);
        return addSection(lineId, upStationId, downStationId, distance);
    }

    private ExtractableResponse<Response> 지하철노선_구간_삭제(Long lineId, Long stationId) {
        return deleteSection(lineId, stationId);
    }

    private void 지하철노선_구간_추가_결과_역이름_검증(Long id) {
        JsonPath jsonPath = findLine(id).jsonPath();
        assertThat(jsonPath.getList("stations.name", String.class)).containsExactly(GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, NOWON_STATION_NAME);
    }

    private void 지하철노선_구간추가_상행역_하행역이_노선에_모두존재시_추가실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.asString()).isEqualTo("구간 상행역, 하행역이 이미 노선에 등록되어 있습니다.");
    }

    private void 지하철노선_구간추가_상행역_하행역이_노선에_모두미존재시_추가실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.asString()).isEqualTo("구간 상행역, 하행역이 노선에 하나도 포함되어있지 않습니다.");
    }

    private void 지하철노선_구간추가_기존구간길이_초과시_추가실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.asString()).isEqualTo("구간길이를 초과했습니다.");
    }

    private Long 지하철노선_하행종점역_식별값_조회(Long id) {
        List<Long> stationIds = findLine(id).response().jsonPath().getList("stations.id", Long.class);
        return stationIds.get(stationIds.size() - 1);
    }

    private Long 지하철노선_하행종점역_이전역_식별값_조회(Long id) {
        List<Long> stationIds = findLine(id).response().jsonPath().getList("stations.id", Long.class);
        return stationIds.get(stationIds.size() - 2);
    }

    private void 지하철노선_구간_삭제_정상_역이름_검증(Long id) {
        JsonPath jsonPath = findLine(id).jsonPath();
        assertThat(jsonPath.getList("stations.name", String.class)).containsExactly(GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME);
    }

    private void 지하철노선_구간_삭제_최소구간일경우_삭제실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.asString()).isEqualTo("구간이 1개인 경우 삭제할 수 없습니다.");
    }

    private void 지하철노선_구간_삭제_노선에존재하지않는_역_삭제실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("구간정보를 찾을 수 없습니다.");
    }

}
