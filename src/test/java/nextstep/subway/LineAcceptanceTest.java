package nextstep.subway;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.common.DependentTest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@DependentTest
@Sql(scripts = {"/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LineAcceptanceTest {

    private static final String 신분당선 = "신분당선";
    private static final String 분당선 = "분당선";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @ParameterizedTest
    @ValueSource(strings = {신분당선, 분당선})
    @DisplayName("지하철 노선을 생성하면 생성된 지하철 노선이 조회된다.")
    void createLine(String name) {
        // when
        LineTestHelper.createLine(name);

        // then
        assertThat(LineTestHelper.selectAllLines())
                .extracting(LineResponse::getName)
                .contains(name);
    }

    /**
     * Given 지하철 역을 생성하고
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선과 역을 함께 찾을 수 있다
     */
    @Test
    @DisplayName("지하철역을 포함하여 노선을 생성하면 지하철역이 포함되어 생성된다")
    void createWithStation() {
        // given
        final String station1 = "야탑역";
        final String station2 = "서현역";

        final StationResponse up = StationTestHelper.createStation(station1);
        final StationResponse down = StationTestHelper.createStation(station2);

        // when
        final LineResponse line = LineTestHelper.createLine(분당선, "bg-red-600", up.getId(), down.getId(), 20);

        // then
        final LineResponse lineResponse = LineTestHelper.selectLine(line.getId());
        assertThat(lineResponse.getStations())
            .extracting(StationResponse::getName)
            .containsExactly(station1, station2);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @Test
    @DisplayName("지하철노선 목록을 조회하면 생성한 지하철 노선이 조회된다.")
    void selectLines() {
        // given
        LineTestHelper.createLine(신분당선);
        LineTestHelper.createLine(분당선);

        // when
        List<LineResponse> lineResponses = LineTestHelper.selectAllLines();

        // then
        assertThat(lineResponses).hasSize(2)
                .map(LineResponse::getName)
                .containsExactly(신분당선, 분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선을 조회하면 생성한 지하철 노선의 정보를 응답받을 수 있다.")
    void selectLine() {
        // given
        LineResponse line = LineTestHelper.createLine(신분당선);

        // when
        LineResponse lineResponse = LineTestHelper.selectLine(line.getId());

        // then
        assertThat(lineResponse.getName()).isEqualTo(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선을 수정하면 해당 지하철 노선 정보는 수정되어 조회된다.")
    void updateLine() {
        // given
        LineResponse line = LineTestHelper.createLine(신분당선);

        // when
        RestAssured
            .given()
                .body(Map.of("name", "1호선", "color", "bg-red-600"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/lines/{id}", line.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        LineResponse lineResponse = LineTestHelper.selectLine(line.getId());

        assertThat(lineResponse)
                .extracting(LineResponse::getName)
                    .isEqualTo("1호선");

        assertThat(lineResponse)
                .extracting(LineResponse::getColor)
                .isEqualTo("bg-red-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선을 삭제하면 해당 지하철 노선 정보는 삭제된다.")
    void deleteLine() {
        // given
        LineResponse line = LineTestHelper.createLine(신분당선);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given()
                .when()
                    .delete("/lines/{id}", line.getId())
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .extract();

        // then
        List<LineResponse> lineResponses = LineTestHelper.selectAllLines();

        assertThat(lineResponses)
                .extracting(LineResponse::getName)
                .doesNotContain(신분당선);
    }


}
