package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.testhelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Map<String, String> 신분당선_강남역_부터_삼성역;
    private Map<String, String> 영호선_강남역_부터_삼성역;
    private Map<String, String> 잠실역_부터_강남역_구간;
    private Map<String, String> 삼성역_부터_선릉역_구간;
    private Map<String, String> 강남역_부터_선릉역_구간;
    private Map<String, String> 강남역_부터_삼성역_구간;
    private Long 잠실역_ID;
    private Long 강남역_ID;
    private Long 삼성역_ID;
    private Long 선릉역_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        StationFixture stationFixture = new StationFixture();
        잠실역_ID = stationFixture.get잠실역_ID();
        강남역_ID = stationFixture.get강남역_ID();
        삼성역_ID = stationFixture.get삼성역_ID();
        선릉역_ID = stationFixture.get선릉역_ID();

        LineFixture lineFixture = new LineFixture(stationFixture);
        신분당선_강남역_부터_삼성역 = lineFixture.get신분당선_강남역_부터_삼성역_params();
        영호선_강남역_부터_삼성역 = lineFixture.get영호선_강남역_부터_삼성역_params();

        SectionFixture sectionFixture = new SectionFixture(stationFixture);

        잠실역_부터_강남역_구간 = sectionFixture.get잠실역_부터_강남역_구간_params();
        삼성역_부터_선릉역_구간 = sectionFixture.get삼성역_부터_선릉역_구간_params();
        강남역_부터_선릉역_구간 = sectionFixture.get강남역_부터_선릉역_구간_params();
        강남역_부터_삼성역_구간 = sectionFixture.get강남역_부터_삼성역_구간_params();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);

        // then
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선들_조회();
        List<String> actual = JsonPathHelper.getAll(response, "name", String.class);
        String expected = "신분당선";
        assertThat(actual).containsAnyOf(expected);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선들의 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        LineApiCaller.지하철_노선_생성(영호선_강남역_부터_삼성역);

        // when
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선들_조회();
        List<String> actual = JsonPathHelper.getAll(response, "name", String.class);

        // then
        String[] expected = {"신분당선", "0호선"};
        assertThat(actual).containsExactly(expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findLine() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        response = LineApiCaller.지하철_노선_조회(location);

        // then
        String actual = JsonPathHelper.getObject(response, "name", String.class);
        String expected = "신분당선";
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        LineUpdateRequest request = new LineUpdateRequest("다른분당선", "bg-red-600");
        LineApiCaller.지하철_노선_수정(request, location);

        // then
        response = LineApiCaller.지하철_노선_조회(location);
        LineResponse actual = JsonPathHelper.getObject(response, ".", LineResponse.class);
        String expectedName = "다른분당선";
        String expectedColor = "bg-red-600";
        assertThat(actual.getName()).isEqualTo(expectedName);
        assertThat(actual.getColor()).isEqualTo(expectedColor);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        LineApiCaller.지하철_노선_삭제(location);

        // then
        response = LineApiCaller.지하철_노선들_조회();
        List<LineResponse> actual = JsonPathHelper.getAll(response, ".", LineResponse.class);
        List<LineResponse> expected = Collections.emptyList();
        assertThat(actual).containsAll(expected);
    }

    /**
     * GIVEN 지하철 노선을 생성하고
     * WHEN 지하철 노선 시작에 구간을 추가하면
     * THEN 수정된 구간을 조회 할 수 있다
     */
    @DisplayName("지하철노선에 시작에 구간을 추가 할 수 있다.")
    @Test
    void updateSections() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        LineApiCaller.지하철_노선에_구간_추가(잠실역_부터_강남역_구간, location);

        // then
        response = LineApiCaller.지하철_노선_조회(location);
        List<Long> actual = JsonPathHelper.getAll(response, "stations.id", Long.class);
        Long[] expected = {잠실역_ID, 강남역_ID, 삼성역_ID};
        assertThat(actual).containsExactly(expected);
    }

    /**
     * GIVEN 지하철 노선을 생성하고
     * WHEN 지하철 노선 끝에 구간을 추가하면
     * THEN 수정된 구간을 조회 할 수 있다
     */
    @DisplayName("지하철노선의 끝에 구간을 추가 할 수 있다.")
    @Test
    void updateSections2() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        LineApiCaller.지하철_노선에_구간_추가(삼성역_부터_선릉역_구간, location);

        // then
        response = LineApiCaller.지하철_노선_조회(location);
        List<Long> actual = JsonPathHelper.getAll(response, "stations.id", Long.class);
        Long[] expected = {강남역_ID, 삼성역_ID, 선릉역_ID};
        assertThat(actual).containsExactly(expected);
    }

    /**
     * GIVEN 지하철 노선을 생성하고
     * WHEN 지하철 노선 중간에 구간을 추가하면
     * THEN 수정된 구간을 조회 할 수 있다
     */
    @DisplayName("지하철노선의 중간에 노선을 추가 할 수 있다.")
    @Test
    void updateSections3() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        LineApiCaller.지하철_노선에_구간_추가(강남역_부터_선릉역_구간, location);

        // then
        response = LineApiCaller.지하철_노선_조회(location);
        List<Long> actual = JsonPathHelper.getAll(response, "stations.id", Long.class);
        Long[] expected = {강남역_ID, 선릉역_ID, 삼성역_ID};
        assertThat(actual).containsExactly(expected);
    }

    /**
     * GIVEN 지하철 노선을 생성하고
     * WHEN 지하철 노선에 이미 추가된 구간을 추가하면
     * THEN 에러 처리와 함께 '이미 추가된 구간입니다.' 라는 메세지가 출력된다
     */
    @DisplayName("새로운 구간이 이미 추가된 역이라면 에러 처리와 함께 '이미 추가된 구간입니다.' 라는 메세지가 출력된다.")
    @Test
    void updateSections4() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        response = given().log().all()
                .body(강남역_부터_삼성역_구간)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(location + "/sections")
                .then().log().all()
                .extract();

        // then
        int actual = response.statusCode();
        int expected = HttpStatus.BAD_REQUEST.value();
        assertThat(actual).isEqualTo(expected);

        String actualBody = response.asString();
        String expectedBody = "이미 추가된 구간입니다.";
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    /**
     * GIVEN 지하철 노선을 생성하고 노선을 수정 후
     * WHEN 지하철 마지막 구간을 제거하면
     * THEN 마지막 구간이 제거된다
     */
    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSections() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");
        LineApiCaller.지하철_노선에_구간_추가(삼성역_부터_선릉역_구간, location);

        // when
        LineApiCaller.지하철_노선_구간_삭제(location, 선릉역_ID.toString());

        // then
        response = LineApiCaller.지하철_노선_조회(location);
        List<Long> actual = JsonPathHelper.getAll(response, "stations.id", Long.class);
        Long[] expected = {강남역_ID, 삼성역_ID};
        assertThat(actual).containsExactly(expected);
    }

    /**
     * GIVEN 지하철 노선을 생성하고 노선을 수정 후
     * WHEN 마지막 구간이 아닌 지하철 구간을 제거하면
     * THEN 에러 처리와 함께 '마지막 구간의 역이 아닙니다.' 라는 메세지가 출력된다.
     */
    @DisplayName("마지막 구간이 아닌 지하철 구간을 제거하면 에러 처리된다.")
    @Test
    void deleteSections2() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");
        LineApiCaller.지하철_노선에_구간_추가(삼성역_부터_선릉역_구간, location);

        // when
        response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", 삼성역_ID.toString())
                .when().delete(location + "/sections")
                .then().log().all()
                .extract();

        // then
        int actual = response.statusCode();
        int expected = HttpStatus.BAD_REQUEST.value();
        assertThat(actual).isEqualTo(expected);

        String actualBody = response.asString();
        String expectedBody = "마지막 구간의 역이 아닙니다.";
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    /**
     * GIVEN 지하철 노선을 시작과 끝만 생성하고
     * WHEN 지하철 마지막 구간을 제거를 시도하면
     * THEN 에러 처리와 함께 '구간이 하나 일 때는 삭제를 할 수 없습니다' 라는 메세지가 출력된다.
     */
    @DisplayName("지하철 노선을 시작과 끝만 생성하고 지하철 마지막 구간을 제거하면 에러 처리된다.")
    @Test
    void deleteSections3() {
        // given
        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(신분당선_강남역_부터_삼성역);
        String location = response.header("location");

        // when
        response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", 삼성역_ID.toString())
                .when().delete(location + "/sections")
                .then().log().all()
                .extract();

        // then
        int actual = response.statusCode();
        int expected = HttpStatus.BAD_REQUEST.value();
        assertThat(actual).isEqualTo(expected);

        String actualBody = response.asString();
        String expectedBody = "구간이 하나 일 때는 삭제를 할 수 없습니다.";
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}
