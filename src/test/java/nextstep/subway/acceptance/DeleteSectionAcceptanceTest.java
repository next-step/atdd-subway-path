package nextstep.subway.acceptance;

import static nextstep.subway.step.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.step.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.step.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.step.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.step.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 삭제 기능")
public class DeleteSectionAcceptanceTest extends AcceptanceTest {

    private Long 강남역;
    private Long 양재역;
    private Long 판교역;
    private Long 신분당선;

    /**
     * Test Setting
     * <p>
     * [신분당선]: up (강남역) ---10--- (양재역) --5-- (판교역) down
     */

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        this.양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        this.판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        CreateLineRequest createLineRequest = new CreateLineRequest("신분당선", "red", 강남역, 양재역, 10);

        this.신분당선 = 지하철_노선_생성_요청(createLineRequest).jsonPath().getLong("id");
        SectionRequest params = new SectionRequest(양재역, 판교역, 5);
        지하철_노선에_지하철_구간_생성_요청(신분당선, params);
    }

    @Test
    @DisplayName("삭제역이 노선의 상행역일때 구간 삭제")
    void 삭제역이_노선의_상행역() {
        assertAll (
            () -> assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역).statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 판교역)
        );
    }

    @Test
    @DisplayName("삭제역이 노선의 하행역일때 구간 삭제")
    void 삭제역이_노선의_하행역() {
        assertAll (
                () -> assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역).statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역)
        );
    }

    @Test
    @DisplayName("구간 개수가 1이하인 노선에 삭제 요청")
    void 구간_개수가_1_이하인_노선에_삭제_요청() {
        CreateLineRequest createLineRequest = new CreateLineRequest("뉴신분당선", "red", 강남역, 양재역, 10);
        Long 구간이_1개인_노선 = 지하철_노선_생성_요청(createLineRequest).jsonPath().getLong("id");

        assertThat(지하철_노선에_지하철_구간_제거_요청(구간이_1개인_노선, 강남역).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("삭제역이 노선의 중간인 경우")
    void 삭제역이_노선의_중간() {
        assertAll (
                () -> assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역).statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역)
        );
    }
}
