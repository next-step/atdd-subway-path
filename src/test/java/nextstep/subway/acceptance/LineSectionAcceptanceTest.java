package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.SectionSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    private final int DEFAULT_SECTION_DISTANCE = 4;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DEFAULT_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 구간을 등록한다.
     * When  기존 A - C 의 구간을 가진 노선에 A , B를 추가하면
     * Then  A -> B, B -> C 노선에 총 두개의 구간이 생긴다.
     */
    @DisplayName("기존 구간의 역을 기준으로 그 사이에 구간 추가.")
    @Test
    void addLineSection2() {
        // 강남역 - 양재역    ->  강남역 - 정자역 - 양재역
        // when
        Long 새로운역_정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 새로운역_정자역, DEFAULT_SECTION_DISTANCE));

        // then

        ExtractableResponse<Response> response = 지하철_노선의_구간목록_조회_요청(신분당선);

        assertAll(
                () -> assertThat(response.jsonPath().getList("distance", Integer.class)).containsExactly(4, 3),
                () -> assertThat(response.jsonPath().getList("upStation.name")).containsExactly("강남역", "정자역")
        );
    }

    /**
     * Given 지하철 노선에 구간을 등록한다.
     * When 새로운 역(B)을 상행 좀점으로 구간을 등록한다.
     * Then A - C 구간에서 B -> A, A -> C 의 구간이 된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addLineSection3() {
        // 강남역 - 양재역    ->  정자역 - 강남역 - 양재역
        // when
        Long 새로운역_정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(새로운역_정자역, 강남역, DEFAULT_SECTION_DISTANCE));


        // then
        ExtractableResponse<Response> response = 지하철_노선의_구간목록_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.jsonPath().getList("distance", Integer.class)).containsExactly(4, 7),
                () -> assertThat(response.jsonPath().getList("upStation.name")).containsExactly("정자역", "강남역")
        );
    }

    /**
     * Given 지하철 노선에 구간을 등록한다.
     * When 새로운 역(B)을 하행 종점으로 구간을 등록한다. (기존과 동일? )
     * Then A - C 구간에서 A -> C -> B 의 구간이 된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addLineSection4() {
        // 강남역 - 양재역    ->  강남역 - 양재역 - 정자
        // when
        Long 새로운역_정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 새로운역_정자역, DEFAULT_SECTION_DISTANCE));

        // then
        ExtractableResponse<Response> response = 지하철_노선의_구간목록_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.jsonPath().getList("distance", Integer.class)).containsExactly(7, 4),
                () -> assertThat(response.jsonPath().getList("upStation.name")).containsExactly("강남역", "양재역")
        );
    }


    @DisplayName("실패케이스1 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSectionFail1() {
        // when
        Long 새로운역_정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 새로운역_정자역, 7));

        // then
        BADREQUEST_실패케이스_검증("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음", response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("실패케이스2 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSectionFail2() {
        // when
        Long 새로운역_정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, DEFAULT_SECTION_DISTANCE));

        // then
        BADREQUEST_실패케이스_검증("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음", response,HttpStatus.BAD_REQUEST);
    }

    @DisplayName("실패케이스3 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSectionFail3() {
        // when
        Long 상행역 = 지하철역_생성_요청("개봉역").jsonPath().getLong("id");
        Long 하행역 = 지하철역_생성_요청("오류역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(상행역, 하행역, DEFAULT_SECTION_DISTANCE));

        // then
        BADREQUEST_실패케이스_검증("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음", response, HttpStatus.BAD_REQUEST);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DEFAULT_SECTION_DISTANCE));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

}
