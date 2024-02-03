package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.acceptance.LineSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@Sql("classpath:db/teardown.sql")
@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void apiCreateLine() {
        // when
        노선이_생성되어_있다("신분당선", "bg-red-600", 1L, 2L);

        // then
        final ExtractableResponse<Response> extract = 노선목록을_조회한다();
        assertThat(extract.jsonPath().getList("name")).contains("신분당선");
        assertThat(extract.jsonPath().getList("color")).contains("bg-red-600");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        노선이_생성되어_있다("신분당선", "bg-red-600", 1L, 2L);
        노선이_생성되어_있다("분당선", "bg-green-600", 1L, 3L);

        // when
        final ExtractableResponse<Response> extract = 노선목록을_조회한다();

        // then
        assertThat(extract.jsonPath().getList("name").size()).isEqualTo(2);
        assertThat(extract.jsonPath().getList("name")).contains("신분당선", "분당선");
        assertThat(extract.jsonPath().getList("color")).contains("bg-red-600", "bg-green-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void apiGetLine() {
        // given
        Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 1L, 2L);

        // when
        final ExtractableResponse<Response> extract = LineSteps.노선을_조회한다(lineId);

        // then
        final LineResponse lineResponse = extract.as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 1L, 2L);

        // when
        노선을_수정한다(lineId, "다른분당선", "bg-red-900");

        // then
        final LineResponse modifyLineResponse = 노선을_조회한다(lineId);
        assertThat(modifyLineResponse.getName()).isEqualTo("다른분당선");
        assertThat(modifyLineResponse.getColor()).isEqualTo("bg-red-900");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Long lineId = 노선이_생성되어_있다("신분당선", "bg-red-600", 1L, 2L);

        // when
        노선을_삭제한다(lineId);

        // then
        노선은_삭제되어_찾을수없다(lineId);
    }

    private static void 노선은_삭제되어_찾을수없다(final Long lineId) {
        final ExtractableResponse<Response> response = LineSteps.노선을_조회한다(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    public static Long 노선이_생성되어_있다(final String name, final String color, final Long upStationId, final Long downStationId) {
        return LineSteps.노선이_생성되어_있다(name, color, upStationId, downStationId).as(LineResponse.class).getId();
    }

    private static LineResponse 노선을_조회한다(final Long lineId) {
        final ExtractableResponse<Response> response = LineSteps.노선을_조회한다(lineId);
        return response.as(LineResponse.class);
    }
}
