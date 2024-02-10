package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    @BeforeEach
    void setUp() {
        StationSteps.createStation("강남역");
        StationSteps.createStation("역삼역");
        StationSteps.createStation("선릉역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineSteps.createLine("신분당선", "bg-red-600", 1L, 2L, 10L);

        // then
        List<String> lineNames = LineSteps.getLineNames();
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineSteps.createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        LineSteps.createLine("분당선", "bg-green-600", 1L, 3L, 10L);

        // when
        List<String> lineNames = LineSteps.getLineNames();

        // then
        assertThat(lineNames).containsAnyOf("신분당선", "분당선");
        assertThat(lineNames).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> line = LineSteps.createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        String locationHeader = line.header("Location");

        // when
        ExtractableResponse<Response> response = LineSteps.getLine(locationHeader);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> line = LineSteps.createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        String locationHeader = line.header("Location");

        // when
        LineSteps.updateLine("다른분당선", "bg-red-600", locationHeader);
        ExtractableResponse<Response> response = LineSteps.getLine(locationHeader);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> line = LineSteps.createLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        String locationHeader = line.header("Location");

        // when
        LineSteps.deleteLine(locationHeader);
        List<String> lineNames = LineSteps.getLineNames();

        // then
        assertThat(lineNames).doesNotContain("신분당선");
    }

}
