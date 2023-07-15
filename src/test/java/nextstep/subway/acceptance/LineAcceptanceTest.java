package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineTestRequests.지하철_노선_목록_조회;
import static nextstep.subway.utils.LineTestRequests.지하철_노선_삭제;
import static nextstep.subway.utils.LineTestRequests.지하철_노선_수정;
import static nextstep.subway.utils.LineTestRequests.지하철_노선_조회;
import static nextstep.subway.utils.LineTestRequests.지하철_노선도_등록;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록;
import static nextstep.subway.utils.StatusCodeAssertions.응답코드_검증;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.utils.DBCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class LineAcceptanceTest {

    @Autowired
    private DBCleanup dbCleanup;

    @BeforeEach
    void init() {
        dbCleanup.execute();
        지하철_역_등록("첫번째역");
        지하철_역_등록("두번째역");
        지하철_역_등록("세번째역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> savedResponse = 지하철_노선도_등록("신분당선",  "bg-red-600", 1L, 2L, 10);
        응답코드_검증(savedResponse, HttpStatus.CREATED);

        //then
        List<LineResponse> lines = 지하철_노선_목록_조회();
        LineResponse 신분당선 = lines.get(0);
        노선도_기댓값_검증(신분당선, 1L, "신분당선", "bg-red-600");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void showLines() {
        //given
        지하철_노선도_등록("신분당선",  "bg-red-600", 1L, 2L, 10);
        지하철_노선도_등록("7호선",  "bg-red-100", 1L, 3L, 10);

        //when
        List<LineResponse> lines = 지하철_노선_목록_조회();

        //then
        assertThat(lines).hasSize(2);

        LineResponse 신분당선 = lines.get(0);
        노선도_기댓값_검증(신분당선, 1L, "신분당선", "bg-red-600");

        LineResponse line7 = lines.get(1);
        노선도_기댓값_검증(line7, 2L, "7호선", "bg-red-100");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다")
    @Test
    void showLine() {
        //given
        ExtractableResponse<Response> response = 지하철_노선도_등록("신분당선",  "bg-red-600", 2L, 3L, 5);
        Long savedId = response.jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> searchResponse = 지하철_노선_조회(savedId);

        //then
        응답코드_검증(searchResponse, HttpStatus.OK);

        LineResponse 신분당선 = searchResponse.jsonPath().getObject("", LineResponse.class);
        노선도_기댓값_검증(신분당선, savedId, "신분당선", "bg-red-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void updateLine() {
        //given
        ExtractableResponse<Response> response = 지하철_노선도_등록("신분당선",  "bg-red-600", 2L, 3L, 5);
        Long savedId = response.jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> updateResponse =  지하철_노선_수정(savedId, "신신분당선", "bg-blue-100");

        //then
        응답코드_검증(updateResponse, HttpStatus.OK);

        LineResponse 신신분당선 = 지하철_노선_조회(savedId).jsonPath().getObject("", LineResponse.class);
        노선도_기댓값_검증(신신분당선, savedId, "신신분당선", "bg-blue-100");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = 지하철_노선도_등록("신분당선",  "bg-red-600", 2L, 3L, 5);
        Long savedId = response.jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> deleteResponse =  지하철_노선_삭제(savedId);

        //then
        응답코드_검증(deleteResponse, HttpStatus.NO_CONTENT);
        ExtractableResponse<Response> notFoundResponse = 지하철_노선_조회(savedId);
        응답코드_검증(notFoundResponse, HttpStatus.NOT_FOUND);
    }

    private void 노선도_기댓값_검증(LineResponse line, Long id, String name, String color) {
        assertAll(
                () -> assertThat(line.getId()).isEqualTo(id),
                () -> assertThat(line.getName()).isEqualTo(name),
                () -> assertThat(line.getColor()).isEqualTo(color),
                () -> assertThat(line.getStations()).hasSize(2)
        );
    }

}
