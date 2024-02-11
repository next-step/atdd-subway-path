package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathStep.지하철_경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.utils.ResponseUtils.응답에서_id_조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private final int 남부터미널_양재_거리 = 3;
    private final int 교대_남부터미널_거리 = 2;
    private final int 교대_강남_거리 = 10;
    private final int 강남_양재_거리 = 10;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 응답에서_id_조회(지하철역_생성_요청("교대역"));
        강남역 = 응답에서_id_조회(지하철역_생성_요청("강남역"));
        양재역 = 응답에서_id_조회(지하철역_생성_요청("양재역"));
        남부터미널역 = 응답에서_id_조회(지하철역_생성_요청("남부터미널역"));

        이호선 = 응답에서_id_조회(지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 교대_강남_거리));
        신분당선 = 응답에서_id_조회(지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 강남_양재_거리));
        삼호선 = 응답에서_id_조회(지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 교대_남부터미널_거리));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 남부터미널_양재_거리));
    }

    /**
     * When 1개 노선에 속한 역의 경로를 조회하면,
     * Then 경로가 조회 된다.
     */
    @Test
    void 단일_노선_내_역_간의_경로를_찾을_수_있다(){
        // when
        final ExtractableResponse<Response> response = 지하철_경로_조회(양재역, 남부터미널역);

        // then
        final List<Station> stations = response.jsonPath().getList("stations", Station.class);
        final int distance = response.jsonPath().getInt("distance");

        assertThat(stations.stream().mapToLong(Station::getId)).containsAll(List.of(양재역, 남부터미널역));
        assertThat(distance).isEqualTo(남부터미널_양재_거리);
    }

    /**
     * Given 공통으로 1개의 역을 가진 2개 노선이 주어진다.
     * When 두개 노선에 걸친 역의 경로를 조회하면,
     * Then 경로가 조회 된다.
     */
    @Test
    void 두_노선에_걸친_역_간의_경로를_찾을_수_있다() {
        // when
        final ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 양재역);

        // then
        final List<Station> stations = response.jsonPath().getList("stations", Station.class);
        final int distance = response.jsonPath().getInt("distance");

        assertThat(stations.stream().mapToLong(Station::getId)).containsAll(List.of(교대역, 남부터미널역, 양재역));
        assertThat(distance).isEqualTo(교대_남부터미널_거리 + 남부터미널_양재_거리);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        final Map<String, String> params = new HashMap<>();

        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", Integer.toString(distance));

        return params;
    }
}

