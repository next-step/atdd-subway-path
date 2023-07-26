package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.UpdateLineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static nextstep.subway.utils.SubwayTestUtil.toSpecificResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql("/station-setup.sql")
class LineAcceptanceTest {

    //    When 지하철 노선을 생성하면
    //    Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        지하철_노선을_생성한다("신분당선", "bg-red-600", 1L, 2L, 10L);

        // then
        List<String> lineNames = 지하철_노선_목록을_조회한다();
        생성된_노선이_노선_목록에_포함된다(lineNames, "신분당선");
    }

    //    Given 2개의 지하철 노선을 생성하고
    //    When 지하철 노선 목록을 조회하면
    //    Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        지하철_노선을_생성한다("신분당선", "bg-red-600", 1L, 2L, 10L);
        지하철_노선을_생성한다("분당선", "bg-green-600", 1L, 3L, 15L);

        // when
        List<String> lineNames = 지하철_노선_목록을_조회한다();

        // then
        생성한_갯수의_지하철_노선_목록을_응답한다(lineNames, 2);
    }

    //    Given 지하철 노선을 생성하고
    //    When 생성한 지하철 노선을 조회하면
    //    Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선을_생성한다("신분당선", "bg-red-600", 1L, 2L, 10L);

        // when
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다(createdResponse.header("Location"));

        // then
        생성한_노선의_정보를_응답한다(createdResponse, selectedResponse);
    }

    //    Given 지하철 노선을 생성하고
    //    When 생성한 지하철 노선을 수정하면
    //    Then 해당 지하철 노선 정보는 수정된다
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선을_생성한다("신분당선", "bg-red-600", 1L, 2L, 10L);

        // when
        지하철_노선을_수정한다(createdResponse.header("Location"), "다른분당선", "bg-red-600");

        // then
        ExtractableResponse<Response> selectedResponse = 지하철_노선을_조회한다(createdResponse.header("Location"));
        조회한_노선은_요청한_정보로_수정된_상태이다(
                selectedResponse,
                "다른분당선",
                "bg-red-600"
        );
    }

    //    Given 지하철 노선을 생성하고
    //    When 생성한 지하철 노선을 삭제하면
    //    Then 해당 지하철 노선 정보는 삭제된다
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선을_생성한다("신분당선", "bg-red-600", 1L, 2L, 10L);

        // when
        지하철_노선을_삭제한다(createdResponse.header("Location"));

        // then
        List<String> lineNames = 지하철_노선_목록을_조회한다();
        삭제한_노선은_조회되지_않는다(lineNames, "신분당선");
    }

    private static void 삭제한_노선은_조회되지_않는다(List<String> lineNames, String deletedName) {
        assertThat(deletedName).isNotIn((lineNames));
    }

    private static void 지하철_노선을_삭제한다(String createdResourceUrl) {
        RestAssured.given().log().all()
                .when().delete(createdResourceUrl)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private static void 조회한_노선은_요청한_정보로_수정된_상태이다(ExtractableResponse<Response> selectedResponse, String updatedName, String updatedColor) {
        assertAll(
                () -> assertThat(updatedName).isEqualTo(toSpecificResponse(selectedResponse, LineResponse.class).getName()),
                () -> assertThat(updatedColor).isEqualTo(toSpecificResponse(selectedResponse, LineResponse.class).getColor())
        );
    }

    private static void 지하철_노선을_수정한다(String createdResourceUrl, String name, String color) {
        RestAssured.given().log().all()
                .body(new UpdateLineRequest(name, color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createdResourceUrl)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    private static void 생성한_노선의_정보를_응답한다(ExtractableResponse<Response> createdResponse, ExtractableResponse<Response> selectedResponse) {
        assertThat(toSpecificResponse(createdResponse, LineResponse.class)).isEqualTo(toSpecificResponse(selectedResponse, LineResponse.class));
    }

    private ExtractableResponse<Response> 지하철_노선을_조회한다(String createdResourceUrl) {
        return RestAssured.given().log().all()
                .when().get(createdResourceUrl)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    private static void 생성한_갯수의_지하철_노선_목록을_응답한다(List<String> lineNames, int createdCount) {
        assertThat(lineNames, hasSize(createdCount));
    }

    private static void 생성된_노선이_노선_목록에_포함된다(List<String> lineNames, String createdLineName) {
        assertThat(lineNames).containsAnyOf(createdLineName);
    }

    private static List<String> 지하철_노선_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name", String.class);
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
