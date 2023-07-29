package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineRemoveSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 논현역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        신분당선 = 지하철_노선_생성_요청(논현역, 양재역).jsonPath().getLong("id");
    }

    /**
     * When 구간이 하나인 경우 지하철역을 제거하면
     * Then 구간을 삭제할 수 없다.
     */
    @DisplayName("지하철 노선에 구간이 하나인 경우 삭제 불가")
    @Test
    void removeSectionWhenRemainOnlyOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 논현역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}