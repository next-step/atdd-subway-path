package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.fixture.LineFixture.createLineCreateParams;
import static nextstep.fixture.SectionFixture.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 삼성역;
    private Long 신논현역;
    private Long 청명역;
    private int distance = 10;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        삼성역 = 지하철역_생성_요청("삼성역").jsonPath().getLong("id");
        신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        청명역 = 지하철역_생성_요청("청명역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, distance));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가하면
     * Then 기존 구간의 상행역에 새로운 구간이 추가되고, 기존 구간의 길이는 새로운 구간 길이를 뺀 나머지 길이이다.
     */
    @Test
    void 지하철_노선에_구간을_등록__기존_구간의_상행역에_새로운_구간을_추가한다() {
        // when
        int 강남_삼성_구간_거리 = 6;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 삼성역, 강남_삼성_구간_거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(신분당선),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).contains(강남역, 삼성역, 양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance),
                () -> assertThat(response.jsonPath().getInt("distance") - 강남_삼성_구간_거리).isEqualTo(4)
        );
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가하면
     * Then 기존 구간의 하행역에 새로운 구간이 추가되고, 기존 구간의 길이는 새로운 구간 길이를 뺀 나머지 길이이다.
     */
    @Test
    void 지하철_노선에_구간을_등록__기존_구간의_하행역에_새로운_구간을_추가한다() {
        int 삼성_양재_구간_거리 = 6;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(삼성역, 양재역, 삼성_양재_구간_거리));

        // when & then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(신분당선),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).contains(강남역, 삼성역, 양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance),
                () -> assertThat(response.jsonPath().getInt("distance") - 삼성_양재_구간_거리).isEqualTo(4)
        );
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가하면
     * Then 신규 구간의 상행역이 노선의 상행 종점으로 등록된다.
     */
    @Test
    void 지하철_노선에_구간을_등록__새로운_구간의_상행역을_상행_종점으로_등록한다() {
        // given
        int 양재역_삼성역_구간_거리 = 20;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 삼성역, 양재역_삼성역_구간_거리));

        // when & then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(신분당선),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).contains(강남역, 양재역, 삼성역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(30)
        );
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가하면
     * Then 신규 구간의 하행역이 노선의 하행 종점으로 등록된다.
     */
    @Test
    void 지하철_노선에_구간을_등록__새로운_구간의_하행역을_하행_종점으로_등록한다() {
        // given
        int 삼성_강남_구간_거리 = 20;
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(삼성역, 강남역, 삼성_강남_구간_거리));

        // when & then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("id")).isEqualTo(신분당선),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).contains(삼성역, 강남역, 양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(30)
        );
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가할때 신규 구간의 거리가 기존 구간의 역 사이 길이보다 크거나 같으면
     * Then 구간을 등록 할 수 없다
     */
    @ParameterizedTest
    @ValueSource(ints = {12, 10})
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록_할_수_없다(int inputDistance) {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 삼성역, inputDistance));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가할때 신규 구간의 상행역과 하행역이 모두 등록되어 있다면
     * Then 구간을 등록 할 수 없다
     */
    @Test
    void 신규_구간이_이미_등록되어_있다면_구간을_등록_할_수_없다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 20));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 구간 사이에 신규 구간을 추가할때 신규 구간의 상행역과 하행역이 기존 구간에 포함되어 있지 않으면
     * Then 구간을 등록 할 수 없다
     */
    @Test
    void 신규_구간의_상행역과_하행역이_기존_구간에_포함되어_있지_않으면_구간을_등록_할_수_없다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 청명역, 20));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간역을 제거 하면
     * Then 노선의 중간역이 제거되고 노선의 거리는 두 구간의 거리의 합이 된다
     */
    @Test
    void 노선의_중간_구간을_제거하면_노선의_거리는_두_구간의_거리의_합이_된다() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 삼성역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 삼성역),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).isNotIn(양재역),
                () -> assertThat(response.jsonPath().getInt("distance")).isEqualTo(20)
        );
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 상행 종점이 제거 되면
     * Then 노선에 구간이 제거된다
     */
    @Test
    void 상행_종점이_제거_되면_노선에_구간이_제거된다() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 삼성역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // & then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 삼성역),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).isNotIn(강남역)
        );
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 하행 종점이 제거 되면
     * Then 노선에 구간이 제거된다
     */
    @Test
    void 하행_종점이_제거_되면_노선에_구간이_제거된다() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 삼성역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 삼성역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).isNotIn(삼성역)
        );
    }

    /**
     * When 구간이 하나인 노선에서 마지막 구간의 상행역을 제거하는 경우
     * Then 지하철 노선의 구간을 제거 할 수 없다
     */
    @Test
    void 구간이_하나인_노선에서_마지막_구간을_제거할때_노선의_구간을_제거_할_수_없다_상행역을_제거하는_경우() {
        // when & then
        assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간이 하나인 노선에서 마지막 구간의 하행역을 제거하는 경우
     * Then 지하철 노선의 구간을 제거 할 수 없다
     */
    @Test
    void 구간이_하나인_노선에서_마지막_구간을_제거할때_노선의_구간을_제거_할_수_없다_하행역을_제거하는_경우() {
        // when & then
        assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 등록되어있지 않은 역을 제거한다면
     * Then 지하철 노선의 구간을 제거 할 수 없다
     */
    @Test
    void 지하철_노선에_등록_되어있지_않은_역을_제거한다면_노선의_구간을_제거_할_수_없다() {
        // when & then
        assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 삼성역).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 새로운 지하철 노선을 생성하고
     * When 지하철 노선의 구간 삭제를 요청하면
     * Then 지하철 노선의 구간을 제거 할 수 없다
     */
    @Test
    void 지하철_노선의_구간이_존재하지_않는다면_노선의_구간을_제거_할_수_없다() {
        // given
        Long 신분당선 = 지하철_노선_생성_요청(createLineCreateParams()).jsonPath().getLong("id");

        // then
        assertThat(지하철_노선에_지하철_구간_제거_요청(신분당선, 청명역).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
