package nextstep.subway.acceptance.line;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.line.LineRequester.*;
import static nextstep.subway.acceptance.station.StationRequester.createStationThenReturnId;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String GANGNAM_STATION_NAME = "강남역";
    private static final String SEOLLEUNG_STATION_NAME = "선릉역";
    private static final String SUWON_STATION_NAME = "수원역";
    private static final String NOWON_STATION_NAME = "노원역";
    private static final String DEARIM_STATION_NAME = "대림역";

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";
    private static final String BUNDANG_LINE_NAME = "분당선";
    private static final String BUNDANG_LINE_COLOR = "bg-green-600";
    private static final String TWO_LINE_NAME = "2호선";
    private static final String TWO_LINE_COLOR = "bg-green-600";
    private static final String THREE_LINE_NAME = "3호선";
    private static final String TRHEE_LINE_COLOR = "bg-blue-600";

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

    @DisplayName("강남역에서 수원역으로 가는 경로 2가지중 선릉역을 경유한 최단거리 경로를 리턴해야한다.")
    @Test
    void 강남역_수원역_최단경로_조회() {
        // given
        Long 강남역 = 지하철역_추가_식별값_리턴(GANGNAM_STATION_NAME);
        Long 선릉역 = 지하철역_추가_식별값_리턴(SEOLLEUNG_STATION_NAME);
        Long 수원역 = 지하철역_추가_식별값_리턴(SUWON_STATION_NAME);
        Long 노원역 = 지하철역_추가_식별값_리턴(NOWON_STATION_NAME);

        Long 신분당선 = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, 강남역, 선릉역, 2);
        Long 이호선 = 지하철노선_생성_후_식별값_리턴(TWO_LINE_NAME, TWO_LINE_COLOR, 선릉역, 수원역, 3);
        Long 삼호선 = 지하철노선_생성_후_식별값_리턴(THREE_LINE_NAME, TRHEE_LINE_COLOR, 강남역, 노원역, 2);

        지하철노선_구간_추가(삼호선, 노원역, 수원역, 3);

        // when
        ExtractableResponse<Response> response = 최단거리조회(강남역, 수원역);

        // then
        강남역_수원역_최단경로_조회_응답값_검증(response);
    }

    @DisplayName("선릉역에서 수원역으로 가는 경로 1가지를 리턴해야한다.")
    @Test
    void 선릉역_수원역_최단경로_조회() {
        // given
        Long 강남역 = 지하철역_추가_식별값_리턴(GANGNAM_STATION_NAME);
        Long 선릉역 = 지하철역_추가_식별값_리턴(SEOLLEUNG_STATION_NAME);
        Long 수원역 = 지하철역_추가_식별값_리턴(SUWON_STATION_NAME);
        Long 노원역 = 지하철역_추가_식별값_리턴(NOWON_STATION_NAME);

        Long 신분당선 = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, 강남역, 선릉역, 2);
        Long 이호선 = 지하철노선_생성_후_식별값_리턴(TWO_LINE_NAME, TWO_LINE_COLOR, 선릉역, 수원역, 3);
        Long 삼호선 = 지하철노선_생성_후_식별값_리턴(THREE_LINE_NAME, TRHEE_LINE_COLOR, 강남역, 노원역, 2);

        지하철노선_구간_추가(삼호선, 노원역, 수원역, 3);

        // when
        ExtractableResponse<Response> response = 최단거리조회(선릉역, 수원역);

        // then
        선릉역_수원역_최단경로_조회_응답값_검증(response);
    }

    @DisplayName("최단경로 조회 역중 노선에 포함되지 않은 역이 존재할 경우 에러를 응답한다.")
    @Test
    void 최단경로_노선_미포함역_조회시_조회실패() {
        // given
        Long 강남역 = 지하철역_추가_식별값_리턴(GANGNAM_STATION_NAME);
        Long 선릉역 = 지하철역_추가_식별값_리턴(SEOLLEUNG_STATION_NAME);
        Long 수원역 = 지하철역_추가_식별값_리턴(SUWON_STATION_NAME);
        Long 노원역 = 지하철역_추가_식별값_리턴(NOWON_STATION_NAME);
        Long 대림역 = 지하철역_추가_식별값_리턴(DEARIM_STATION_NAME);

        Long 신분당선 = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, 강남역, 선릉역, 2);
        Long 이호선 = 지하철노선_생성_후_식별값_리턴(TWO_LINE_NAME, TWO_LINE_COLOR, 선릉역, 수원역, 3);
        Long 삼호선 = 지하철노선_생성_후_식별값_리턴(THREE_LINE_NAME, TRHEE_LINE_COLOR, 강남역, 노원역, 2);

        지하철노선_구간_추가(삼호선, 노원역, 수원역, 3);

        // when
        ExtractableResponse<Response> response = 최단거리조회(선릉역, 대림역);

        // then
        최단경로_노선_미포함역_조회시_조회실패_응답값_검증(response);
    }

    @DisplayName("최단경로 조회 시작역, 종착역이 동일할 경우 에러를 응답한다.")
    @Test
    void 최단경로_시작역_종착역_동일할경우_조회실패() {
        // given
        Long 강남역 = 지하철역_추가_식별값_리턴(GANGNAM_STATION_NAME);
        Long 선릉역 = 지하철역_추가_식별값_리턴(SEOLLEUNG_STATION_NAME);
        Long 수원역 = 지하철역_추가_식별값_리턴(SUWON_STATION_NAME);
        Long 노원역 = 지하철역_추가_식별값_리턴(NOWON_STATION_NAME);
        Long 대림역 = 지하철역_추가_식별값_리턴(DEARIM_STATION_NAME);

        Long 신분당선 = 지하철노선_생성_후_식별값_리턴(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, 강남역, 선릉역, 2);
        Long 이호선 = 지하철노선_생성_후_식별값_리턴(TWO_LINE_NAME, TWO_LINE_COLOR, 선릉역, 수원역, 3);
        Long 삼호선 = 지하철노선_생성_후_식별값_리턴(THREE_LINE_NAME, TRHEE_LINE_COLOR, 강남역, 노원역, 2);

        지하철노선_구간_추가(삼호선, 노원역, 수원역, 3);

        // when
        ExtractableResponse<Response> response = 최단거리조회(대림역, 대림역);

        // then
        최단경로_시작역_종착역_동일할경우_조회실패_응답값_검증(response);
    }


    private Long 지하철노선_생성_후_식별값_리턴(String name, String color, String upStationName, String downStationName, int distance) {
        return createLineThenReturnId(name, color, upStationName, downStationName, distance);
    }

    private Long 지하철노선_생성_후_식별값_리턴(String name, String color, Long upStationId, Long downStationId, int distance) {
        return createLineThenReturnId(name, color, upStationId, downStationId, distance);
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

    private ExtractableResponse<Response> 지하철노선_구간_추가(Long lineId, Long upStationId, Long downStationId, int distance) {
        return addSection(lineId, upStationId, downStationId, distance);
    }

    private ExtractableResponse<Response> 지하철노선_구간_추가(Long lineId, String upStationName, String downStationName, int distance) {
        Long upStationId = createStationThenReturnId(upStationName);
        Long downStationId = createStationThenReturnId(downStationName);
        return addSection(lineId, upStationId, downStationId, distance);
    }

    private ExtractableResponse<Response> 지하철노선_구간_삭제(Long lineId, Long stationId) {
        return deleteSection(lineId, stationId);
    }

    private ExtractableResponse<Response> 최단거리조회(Long startStationId, Long endStationId) {
        return findShortPath(startStationId, endStationId);
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

    private void 강남역_수원역_최단경로_조회_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly(GANGNAM_STATION_NAME, SEOLLEUNG_STATION_NAME, SUWON_STATION_NAME);
        assertThat(response.jsonPath().getObject("distance", Integer.class)).isEqualTo(5);
    }

    private void 선릉역_수원역_최단경로_조회_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly(SEOLLEUNG_STATION_NAME, SUWON_STATION_NAME);
        assertThat(response.jsonPath().getObject("distance", Integer.class)).isEqualTo(3);
    }

    private void 최단경로_노선_미포함역_조회시_조회실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.asString()).isEqualTo("노선에 역이 존재하지 않습니다.");
    }

    private void 최단경로_시작역_종착역_동일할경우_조회실패_응답값_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("최단경로 시작역, 종착역이 동일할 수 없습니다.");
    }

}
