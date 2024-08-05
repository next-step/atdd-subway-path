package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.presentation.LineRequest;
import nextstep.subway.presentation.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철_역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "classpath:truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LineAcceptanceTest {
    private Long 강남역_ID;
    private Long 신논현역_ID;
    private Long 신사역_ID;
    private LineRequest 신분당선_request;

    @BeforeEach
    void setup() {
        강남역_ID = 지하철_역_생성("강남역").body().jsonPath().getLong("id");
        신논현역_ID = 지하철_역_생성("신논현역").body().jsonPath().getLong("id");
        신사역_ID = 지하철_역_생성("신사역").body().jsonPath().getLong("id");
        신분당선_request = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 신논현역_ID, 10);
    }

    /**
     * Given: 새로운 지하철 노선 정보를 입력하고,
     * When: 관리자가 노선을 생성하면,
     * Then: 해당 노선이 생성되고 노선 목록에 포함된다.
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        LineRequest newLine = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 신논현역_ID, 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성(newLine);

        // then
        List<String> allLineNames = 지하철_노선_내_전체_지하철_역_이름_찾기();
        assertThat(allLineNames).containsAnyOf("신분당선");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given: 여러 개의 지하철 노선이 등록되어 있고,
     * When: 관리자가 지하철 노선 목록을 조회하면,
     * Then: 모든 지하철 노선 목록이 반환된다.
     */
    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void retrieveAllLines() {
        // given
        LineRequest _5호선_request = new LineRequest("5호선", "bg-purple-400", 강남역_ID, 신사역_ID, 10);

        지하철_노선_생성(신분당선_request);
        지하철_노선_생성(_5호선_request);

        // when
        List<String> allLineNames = 지하철_노선_내_전체_지하철_역_이름_찾기();

        // then
        assertThat(allLineNames).contains("신분당선", "5호선");
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 조회하면,
     * Then: 해당 노선의 정보가 반환된다.
     */
    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void retrieveLine() {
        // given
        LineRequest _5호선_request = new LineRequest("5호선", "bg-purple-400", 강남역_ID, 신사역_ID, 10);
        지하철_노선_생성(신분당선_request);

        ExtractableResponse<Response> response = 지하철_노선_생성(_5호선_request);
        Long _5호선_id = response.body().jsonPath().getLong("id");

        // when
        LineResponse findLine = 노선_아이디로_지하철_노선_찾기(_5호선_id);

        // then
        assertThat(findLine.getName()).isEqualTo("5호선");
        assertThat(findLine.getColor()).isEqualTo("bg-purple-400");
        assertThat(findLine.getStations().size()).isEqualTo(2);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 수정하면,
     * Then: 해당 노선의 정보가 수정된다.
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // given
        ExtractableResponse<Response> response = 지하철_노선_생성(신분당선_request);
        Long 신분당선_ID = Long.valueOf(response.body().jsonPath().getString("id"));

        // when
        지하철_노선_정보_수정(신분당선_ID, "2호선", "bg-red-700");
        LineResponse findLine = 노선_아이디로_지하철_노선_찾기(신분당선_ID);

        // then
        assertThat(findLine.getName()).isEqualTo("2호선");
        assertThat(findLine.getColor()).isEqualTo("bg-red-700");
        assertThat(findLine.getStations().size()).isEqualTo(2);
    }


    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 삭제하면,
     * Then: 관리자가 해당 노선을 삭제하면,
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = 지하철_노선_생성(신분당선_request);
        String 신분당선_ID = response.body().jsonPath().getString("id");

        // when
        지하철_노선_삭제(신분당선_ID);
        List<String> allLineNames = 지하철_노선_내_전체_지하철_역_이름_찾기();

        // then
        assertThat(allLineNames).doesNotContain("신분당선");
        assertThat(allLineNames.size()).isEqualTo(0);
    }
}
