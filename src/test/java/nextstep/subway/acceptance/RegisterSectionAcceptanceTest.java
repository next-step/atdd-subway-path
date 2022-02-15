package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 등록 기능")
public class RegisterSectionAcceptanceTest extends AcceptanceTest {

    private Long 강남역;
    private Long 양재역;
    private Long 판교역;
    private Long 신분당선;

    /**
     * Test Setting
     * <p>
     * [신분당선]: up (강남역) ---10--- (양재역) down
     */

    @BeforeEach
    public void setUp() {
        super.setUp();
        this.강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        this.양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        this.판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        CreateLineRequest createLineRequest = new CreateLineRequest("신분당선", "red", 강남역, 양재역, 10);

        this.신분당선 = 지하철_노선_생성_요청(createLineRequest).jsonPath().getLong("id");
    }

    @Test
    @DisplayName("역 사이에 새로운 역 등록")
    void 역_사이에_새로운_역_등록() {
        SectionRequest params = new SectionRequest(판교역, 양재역, 5);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록")
    void 새로운_역을_상행_종점으로_등록() {
        SectionRequest params = new SectionRequest(판교역, 강남역, 10);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록")
    void 새로운_역을_하행_종점으로_등록() {
        SectionRequest params = new SectionRequest(양재역, 판교역, 10);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선 조회시 구간에 등록된 순서대로 조회")
    void 노선_조회시_구간에_등록된_순서대로_조회() {
        SectionRequest params = new SectionRequest(판교역, 양재역, 5);
        지하철_노선에_지하철_구간_생성_요청(신분당선, params);
        List<Long> list = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);

        assertThat(list).containsExactly(강남역, 판교역, 양재역);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void 등록_예외_기존_역_사이_길이보다_크거나_같은경우() {
        SectionRequest params = new SectionRequest(판교역, 양재역, 15);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void 등록_예외_상행역_하행역_이미_모두_등록된_경우() {
        SectionRequest params = new SectionRequest(양재역, 강남역, 5);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void 등록_예외_상행역_하행역_하나도_안포함된_경우() {
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        Long 잠실역 = 지하철역_생성_요청("잠실역").jsonPath().getLong("id");
        SectionRequest params = new SectionRequest(교대역, 잠실역, 5);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
