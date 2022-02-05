package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath()
                               .getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath()
                               .getLong("id");

        LineRequest lineRequest = createLineCreateParams(
                강남역,
                양재역
        );
        신분당선 = 지하철_노선_생성_요청(lineRequest).jsonPath()
                                        .getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                                    .getLong("id");
        지하철_노선에_지하철_구간_생성_요청(
                신분당선,
                createSectionCreateParams(
                        양재역,
                        정자역
                )
        );

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                           .getList(
                                   "stations.id",
                                   Long.class
                           )).containsExactly(
                강남역,
                양재역,
                정자역
        );
    }


    @DisplayName("구간 추가 - 상행역이 같은 경우에 새로운 역을 등록")
    @Test
    void addLineSection2() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                                    .getLong("id");


        // when
        지하철_노선에_지하철_구간_생성_요청(
                신분당선,
                createSectionCreateParams(
                        강남역,
                        정자역,
                        4
                )
        );

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath()
                           .getList(
                                   "stations.id",
                                   Long.class
                           )).containsExactly(
                강남역,
                정자역,
                양재역
        );
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
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath()
                                    .getLong("id");
        지하철_노선에_지하철_구간_생성_요청(
                신분당선,
                createSectionCreateParams(
                        양재역,
                        정자역
                )
        );

        // when
        지하철_노선에_지하철_구간_제거_요청(
                신분당선,
                정자역
        );

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath()
                           .getList(
                                   "stations.id",
                                   Long.class
                           )).containsExactly(
                강남역,
                양재역
        );
    }

    private LineRequest createLineCreateParams(Long upStationId, Long downStationId) {
        return new LineRequest(
                "신분당선",
                "bg-red-600",
                upStationId,
                downStationId,
                7
        );
    }

    private LineRequest createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        return new LineRequest(
                "신분당선",
                "bg-red-600",
                upStationId,
                downStationId,
                distance
        );
    }

    private SectionRequest createSectionCreateParams(Long upStationId, Long downStationId) {
        return new SectionRequest(
                upStationId,
                downStationId,
                6
        );
    }

    private SectionRequest createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(
                upStationId,
                downStationId,
                distance
        );
    }
}
