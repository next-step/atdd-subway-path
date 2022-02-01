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
class LineSectionAcceptanceTest extends AcceptanceTest {
    Long 신분당선;
    Long 강남역;
    Long 양재역;
    int 강남_양재_거리 = 7;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        final 노선_생성_파라미터 노선_생성_파라미터 = new 노선_생성_파라미터(강남역, 양재역, 강남_양재_거리);
        신분당선 = 지하철_노선_생성_요청(노선_생성_파라미터).jsonPath().getLong("id");
    }

    /**
     * When 상행역을 기준으로 역 사이에 새로운 역으로 구간 등록을 요청하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("상행역을 기준으로 역 사이에 새로운 역으로 구간을 등록")
    @Test
    void addStationBetweenStationsBasedOnUpStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(역_아이디_목록_추출(response)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 하행역을 기준으로 역 사이에 새로운 역을 등록한다
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("하행역을 기준으로 역 사이에 새로운 역으로 구간을 등록")
    @Test
    void addStationBetweenStationsBasedOnDownStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(정자역, 양재역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(역_아이디_목록_추출(response)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * 새로운 역을 상행 종점으로 등록한다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addStationBaseOnLastUpStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(정자역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(역_아이디_목록_추출(response)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * 새로운 역을 하행 종점으로 등록한다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addStationBaseOnLastDownStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(역_아이디_목록_추출(response)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 노선의 역 목록을 요청하면
     * Then 상행역부터 하행역까지 정렬된 목록을 받는다.
     */
    @DisplayName("노선의 정렬된 역 목록 요청")
    @Test
    void getStationsOfLineSortedUpToDown() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(정자역, 강남역));
        지하철_노선에_지하철_구간_생성_요청(신분당선, new 구간_생성_파라미터(판교역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(역_아이디_목록_추출(response)).containsExactly(판교역, 정자역, 강남역, 양재역);
    }

}
