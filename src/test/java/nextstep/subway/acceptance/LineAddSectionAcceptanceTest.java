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

@DisplayName("지하철 구간 추가 기능")
public class LineAddSectionAcceptanceTest extends AcceptanceTest {
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
     * When 지하철 노선에 상행역, 하행역이 모두 없는 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 노선에 상행역, 하행역이 모두 없는 구간을 등록")
    @Test
    void addLineSectionNotExistStations() {
        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 판교역, 정자역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 상행역, 하행역이 모두 있는 구간 추가를 요청하면
     * Then 노선에 새로운 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 노선에 상행역, 하행역이 모두 있는 구간을 등록")
    @Test
    void addLineSectionExistStations() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 논현역, 양재역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 신규 생성하는 구간의 길이가 기존 길이보다 크거나 같으면
     * Then 노선에 새로운 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 노선에 기존 구간보다 긴 구간 등록 불가")
    @Test
    void addLineSectionLongDistance() {
        // when
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, 논현역, 강남역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 새로운 역을 상행 종점으로 추가 요청하면
     * Then 노선에 새로운 구간이 추가 된다.
     */
    @DisplayName("지하철 노선에 새로운 역을 하행 종점으로 구간 추가")
    @Test
    void addLineSectionLineUpStation() {
        // when
        Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 신사역, 논현역, 10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신사역, 논현역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 역을 하행 종점으로 추가 요청하면
     * Then 노선에 새로운 구간이 추가 된다.
     */
    @DisplayName("지하철 노선에 새로운 역을 하행 종점으로 구간 추가")
    @Test
    void addLineSectionLineDownStation() {
        // when
        Long 양재시민의숲역 = 지하철역_생성_요청("양재시민의숲역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 양재역, 양재시민의숲역, 10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(논현역, 양재역, 양재시민의숲역);
    }

    /**
     * When 지하철 노선에 역 사이에 새로운 구간을 추가 요청하면
     * Then 노선에 새로운 구간이 추가 된다.
     */
    @DisplayName("지하철 노선에 새로운 역을 하행 종점으로 구간 추가")
    @Test
    void addLineSectionAtMiddle() {
        // when
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 논현역, 강남역, 4);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(논현역, 강남역, 양재역);
    }
}
