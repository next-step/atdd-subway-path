package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private static final int DEFAULT_DISTANCE = 10;

    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, DEFAULT_DISTANCE).jsonPath().getLong("id");
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 정자역, 3);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 구간 사이의 구간을 추가한다.
     * Then 노선에 새로운 구간이 추가된다.
     */
    @DisplayName("지하철 노선에 사이에 구간 등록")
    @Test
    void addLineSectionBetween() {
        //when
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역, 논현역, DEFAULT_DISTANCE / 2);
        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 논현역, 양재역);
        assertThat(response.jsonPath().getList("sections.distance", Integer.class)).containsOnly(DEFAULT_DISTANCE / 2, DEFAULT_DISTANCE / 2);
    }


    /**
     * When 길이가 더 긴 구간을 구간 사이에 추가한다.
     * Then 오류가 발생한다.
     */
    @DisplayName("지하철 노선에 사이에 더 긴 구간 등록")
    @Test
    void addLineSectionBetweenOverDistance() {
        //when
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        //then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 강남역, 논현역, DEFAULT_DISTANCE * 2);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 겹치는 역이 없는 구간을 추가한다.
     * Then 오류가 발생한다..
     */
    @DisplayName("지하철 노선에 사이에 겹치지 않는 구간 등록")
    @Test
    void addLineSectionBetweenNotOverlapStation() {
        //when
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        Long 옥수역 = 지하철역_생성_요청("옥수역").jsonPath().getLong("id");

        //then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 옥수역, 논현역, DEFAULT_DISTANCE);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 정자역, DEFAULT_DISTANCE / 2);

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 사이의 역 제거를 요청 하면
     * Then 노선에 구간이 제거되고, 구간의 길이가 합쳐진다.
     */
    @DisplayName("지하철 노선에 사이의 역을 삭제한다")
    @Test
    void removeIntervalStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 정자역, DEFAULT_DISTANCE / 2);

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        assertThat(response.jsonPath().getList("sections.distance", Integer.class))
                .containsExactly(DEFAULT_DISTANCE + (DEFAULT_DISTANCE / 2));
    }

    /**
     * When 지하철 노선의 사이의 역 제거를 요청 하면
     * Then 노선에 구간이 제거되고, 구간의 길이가 합쳐진다.
     */
    @DisplayName("구간이 1개인 지하철 노선의 역을 삭제한다")
    @Test
    void removeStationWhenLineHasOneSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

}
