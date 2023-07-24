package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.CreateSectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.utils.SubwayTestUtil.toSpecificResponse;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql("/station-setup.sql")
public class SectionAcceptanceTest {
    private static String CREATE_LINE_URL;
    private static final Long UP_STATION_ID = 2L;
    private static final Long DOWN_STATION_ID = 3L;
    private static final Long DISTANCE = 5L;

    @BeforeEach
    void init() {
        ExtractableResponse<Response> createdResponse = 지하철_노선을_생성한다("신분당선", "bg-red-600", 1L, 2L, 10L);
        CREATE_LINE_URL = createdResponse.header("Location");
    }

    /**
     * When 지하철 구간을 추가하면
     * Then 지하철 노선 조회시, 추가된 구간을 확인할 수 있다
     */
    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void createSection() {
        ExtractableResponse<Response> createdResponse = 지하철_구간을_추가한다(UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다();

        추가한_구간이_노선에_포함된다(createdResponse, selectedResponse);
    }

    /**
     * Given 하행역이 지하철 노선에 등록된 구간으로
     * When 구간을 등록하면
     * Then 에러가 발생한다
     */
    @DisplayName("하행역이 이미 지하철 노선에 등록된 지하철 구간을 추가하면 에러가 발생한다.")
    @Test
    void createSection_already_register() {
        CreateSectionRequest request = 하행역이_지하철_노선에_등록된_구간에_대한_요청이_존재한다();

        구간을_등록하면_에러가_발생한다(request);
    }

    /**
     * Given 상행역이 노선의 하행 종점역이 아닌 구간으로
     * When 구간을 등록하면
     * Then 에러가 발생한다
     */
    @DisplayName("상행역이 노선의 하행 종점역이 아닌 지하철 구간을 추가하면 에러가 발생한다.")
    @Test
    void createSection_not_include() {
        CreateSectionRequest request = 상행역이_노선의_하행_종점역이_아닌_구간에_대한_요청이_존재한다();

        구간을_등록하면_에러가_발생한다(request);
    }

    /**
     * Given 새로운 지하철 구간을 추가하고
     * When 해당 노선의 구간을 제거하면
     * Then 마지막에 추가된 구간이 제거된다.
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다();
        ExtractableResponse<Response> createdResponse = 지하철_구간을_추가한다(UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

        지하철_구간을_삭제한다(createdResponse.header("Location") + "/sections?stationId=" + DOWN_STATION_ID);

        삭제한_구간의_역이_노선에서_제외된다();
    }

    /**
     * Given 구간이 1개 뿐인 노선이 있고
     * When 구간을 제거하면
     * Then 에러가 발생한다
     */
    @DisplayName("구간이 1개 뿐인 노선의 구간을 제거하면 에러가 발생한다.")
    @Test
    void deleteSection_just_one_section() {
        ExtractableResponse<Response> selectedResponse = 구간이_1개_뿐인_노선이_있다();

        구간을_삭제하면_에러가_발생한다(
                CREATE_LINE_URL +
                        "/sections?stationId=" +
                        toSpecificResponse(selectedResponse, LineResponse.class)
                                .getStations()
                                .get(1)
                                .getId());
    }

    /**
     * Given 새로운 지하철 구간을 추가하고
     * When 노선의 마지막 구간(하행 종점역)이 아닌 구간을 제거하면
     * Then 에러가 발생한다
     */
    @DisplayName("노선의 마지막 구간이 아닌 구간을 제거하면 에러가 발생한다.")
    @Test
    void deleteSection_not_down_end_station() {
        ExtractableResponse<Response> createdResponse = 지하철_구간을_추가한다(UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

        Long stationId = 노선의_마지막_구간이_아닌_구간의_하행역_Id이다(createdResponse);

        구간을_삭제하면_에러가_발생한다(createdResponse.header("Location") + "/sections?stationId=" + stationId);
    }

    private static Long 노선의_마지막_구간이_아닌_구간의_하행역_Id이다(ExtractableResponse<Response> createdResponse) {
        return toSpecificResponse(createdResponse, LineResponse.class).getStations().get(0).getId();
    }

    private static ExtractableResponse<Response> 구간이_1개_뿐인_노선이_있다() {
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다();
        assertThat(toSpecificResponse(selectedResponse, LineResponse.class).getStations().size()).isEqualTo(2);
        return selectedResponse;
    }

    private static void 구간을_삭제하면_에러가_발생한다(String resourceUrl) {
        RestAssured.given().log().all()
                .when().delete(resourceUrl)
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 삭제한_구간의_역이_노선에서_제외된다() {
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다();
        assertThat(new StationResponse(DOWN_STATION_ID, "또 다른 지하철역"))
                .isNotIn(toSpecificResponse(selectedResponse, LineResponse.class).getStations());
    }

    private static void 지하철_구간을_삭제한다(String resourceUrl) {
        RestAssured.given().log().all()
                .when().delete(resourceUrl)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private static void 구간을_등록하면_에러가_발생한다(CreateSectionRequest request) {
        RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(CREATE_LINE_URL + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static CreateSectionRequest 상행역이_노선의_하행_종점역이_아닌_구간에_대한_요청이_존재한다() {
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다();
        Long lineUpStationId = toSpecificResponse(selectedResponse, LineResponse.class).getStations().get(0).getId();
        return new CreateSectionRequest(lineUpStationId, DOWN_STATION_ID, DISTANCE);
    }

    private static CreateSectionRequest 하행역이_지하철_노선에_등록된_구간에_대한_요청이_존재한다() {
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다();
        Long alreadyRegisteredStationId = toSpecificResponse(selectedResponse, LineResponse.class).getStations().get(0).getId();
        return new CreateSectionRequest(UP_STATION_ID, alreadyRegisteredStationId, DISTANCE);
    }

    private static void 추가한_구간이_노선에_포함된다(ExtractableResponse<Response> createdResponse, ExtractableResponse<Response> selectedResponse) {
        assertThat(toSpecificResponse(createdResponse, LineResponse.class)).isEqualTo(toSpecificResponse(selectedResponse, LineResponse.class));
    }

    private static ExtractableResponse<Response> 지하철_노선을_조회한다() {
        return RestAssured.given().log().all()
                .when().get(CREATE_LINE_URL)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    private static ExtractableResponse<Response> 지하철_구간을_추가한다(Long upStationId, Long downStationId, Long distance) {
        CreateSectionRequest request = new CreateSectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(CREATE_LINE_URL + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    private static ExtractableResponse<Response> 지하철_노선을_생성한다(String name, String color, Long upStationId, Long downStationId, Long distance) {
        CreateLineRequest request = new CreateLineRequest(
                name,
                color,
                upStationId,
                downStationId,
                distance
        );

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }
}
