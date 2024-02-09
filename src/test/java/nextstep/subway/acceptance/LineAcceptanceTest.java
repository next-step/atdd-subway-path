package nextstep.subway.acceptance;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.AcceptanceMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * when 지하철 노선을 생성하면
     * then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        // when
        Long stationId1 = makeStation("gangnam").jsonPath().getLong("id");
        Long stationId2 = makeStation("yeoksam").jsonPath().getLong("id");

        ExtractableResponse<Response> response = makeLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10L));

        // then
        List<Long> ids = getLines().jsonPath().getList("id", Long.class);

        assertThat(ids).containsOnly(response.jsonPath().getLong("id"));
    }

    /**
     * given 2개의 지하철 노선을 생성하고
     * when 지하철 노선 목록을 조회하면
     * then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        // given
        Long stationId1 = makeStation("gangnam").jsonPath().getLong("id");
        Long stationId2 = makeStation("yeoksam").jsonPath().getLong("id");
        Long stationId3 = makeStation("samseong").jsonPath().getLong("id");

        Long lineId1 = makeLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10L)).jsonPath().getLong("id");
        Long lineId2 = makeLine(new LineRequest("분당선", "bg-green-600", stationId1, stationId3, 7L)).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = getLines();

        // then
        assertThat(response.jsonPath().getList("id", Long.class)).containsOnly(lineId1, lineId2);
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 조회하면
     * then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 단일 조회")
    @Test
    void showLine() {
        Long stationId1 = makeStation("gangnam").jsonPath().getLong("id");
        Long stationId2 = makeStation("yeoksam").jsonPath().getLong("id");

        // given
        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10L)).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = getLine(lineId);

        // then
        assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId);
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 수정하면
     * then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Long stationId1 = makeStation("gangnam").jsonPath().getLong("id");
        Long stationId2 = makeStation("yeoksam").jsonPath().getLong("id");

        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10L)).jsonPath().getLong("id");

        // when
        Map<String, String> request = Map.of("name", "다른분당선", "color", "bg-red-600");

        RestAssured
                .given().log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .put("/lines/" + lineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> response = getLine(lineId);

        assertThat(response.jsonPath().getString("name")).isEqualTo("다른분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 삭제하면
     * then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        Long stationId1 = makeStation("gangnam").jsonPath().getLong("id");
        Long stationId2 = makeStation("yeoksam").jsonPath().getLong("id");

        // given
        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10L)).jsonPath().getLong("id");

        // when
        RestAssured
                .given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> response = getLines();
        assertThat(response.jsonPath().getList("id", Long.class)).doesNotContain(lineId);
    }
}
