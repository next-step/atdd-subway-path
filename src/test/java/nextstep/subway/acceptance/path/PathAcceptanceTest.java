package nextstep.subway.acceptance.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.helper.api.StationApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.helper.api.LineApi.구간생성요청;
import static nextstep.subway.helper.api.LineApi.노선생성요청;
import static nextstep.subway.helper.api.PathApi.최단경로조회요청;
import static nextstep.subway.helper.fixture.LineFixture.*;
import static nextstep.subway.helper.fixture.SectionFixture.추가구간_생성_바디;
import static nextstep.subway.helper.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest {
    private static Long 신사역ID;
    private static Long 논현역ID;
    private static Long 신논현역ID;
    private static Long 고속터미널역ID;
    private static Long 한남역ID;
    private static Long 서빙고역ID;

    private static Long 신분당선ID;

    /**
     *                           (distance 5)
     *                신사역 ------ *신분당선* ------ 논현역
     *                  |                     /     |
     *                  |     (distance 9) /        |
     * (distance 10) *3호선*         *7호선*       *신분당선* (distance 10)
     *                  |         /                 |
     *                  |      /                    |
     *               고속터미널역 ---- *9호선* ------ 신논현역
     *                          (distance 12)
     *
     *                  한남역 --- *경의중앙선* --- 서빙고역
     *                          (distance 14)
     */
    @BeforeEach
    void setup() {
        신사역ID = StationApi.create(신사역_생성_바디).getLong("id");
        논현역ID = StationApi.create(논현역_생성_바디).getLong("id");
        신논현역ID = StationApi.create(신논현역_생성_바디).getLong("id");
        고속터미널역ID = StationApi.create(고속터미널역_생성_바디).getLong("id");
        한남역ID = StationApi.create(한남역_생성_바디).getLong("id");
        서빙고역ID = StationApi.create(서빙고역_생성_바디).getLong("id");

        신분당선ID = 노선생성요청(신분당선_생성_바디(논현역ID, 신논현역ID)).getLong("id");
        노선생성요청(구호선_생성_바디(고속터미널역ID, 신논현역ID)).getLong("id");
        노선생성요청(삼호선_생성_바디(고속터미널역ID, 신사역ID)).getLong("id");
        노선생성요청(칠호선_생성_바디(고속터미널역ID, 논현역ID)).getLong("id");
        노선생성요청(경의중앙선_생성_바디(한남역ID, 서빙고역ID)).getLong("id");

        구간생성요청(신분당선ID, 추가구간_생성_바디(신사역ID, 논현역ID, 5));
    }

    @Test
    @DisplayName("연결된 출발역과 도착역의 최단 경로에 걸친 역들 정보와 총 거리를 알 수 있다.")
    void succeedForLinkedPath() {
        List<String> 포함된역_예상결과 = List.of("신사역", "논현역", "신논현역");
        int 거리_예상결과 = 15;

        ExtractableResponse<Response> 요청결과 = 최단경로조회요청(신사역ID, 신논현역ID);

        List<String> 포함된역_반환결과 = 요청결과.jsonPath().getList("stations.name", String.class);
        int 거리_반환결과 = 요청결과.jsonPath().getInt("distance");

        assertThat(요청결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(포함된역_반환결과).containsExactlyElementsOf(포함된역_예상결과);
        assertThat(거리_반환결과).isEqualTo(거리_예상결과);
    }

    @Test
    @DisplayName("주어진 출발역과 도착역이 연결되지 않은 경우 0개의 역이 반환된다.")
    void succeedForUnlinkedPath() {
        int 거리_예상결과 = -1;

        ExtractableResponse<Response> 요청결과 = 최단경로조회요청(신사역ID, 한남역ID);

        List<String> 포함된역_반환결과 = 요청결과.jsonPath().getList("stations.name", String.class);
        int 거리_반환결과 = 요청결과.jsonPath().getInt("distance");

        assertThat(요청결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(포함된역_반환결과).isEmpty();
        assertThat(거리_반환결과).isEqualTo(거리_예상결과);
    }

    @Test
    @DisplayName("존재하지 않는 역에 대한 경로 조회 시도시 에러가 발생한다.")
    void failForNotFoundStation() {
        Long 미존재역ID = 10000L;

        ExtractableResponse<Response> 요청결과 = 최단경로조회요청(신사역ID, 미존재역ID);

        assertThat(요청결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("주어진 출발역과 도착역이 동일한 경우 에러가 발생한다.")
    void failForSameSourceAndTarget() {
        ExtractableResponse<Response> 요청결과 = 최단경로조회요청(신사역ID, 신사역ID);

        assertThat(요청결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
