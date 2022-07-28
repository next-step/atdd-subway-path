package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * when 출발역 ID와 도착역 ID로 지하철 경로조회 요청을 하면
     * then 거리의 합이 가장 작은 경로를 구할 수 있다
     */
    @DisplayName("최단 경로 경로 조회")
    @Test
    void findShortestPath() {
        var 칠호선 = 지하철_노선_생성_요청("7호선", "olive").jsonPath().getLong("id");
        var 구호선 = 지하철_노선_생성_요청("9호선", "gold").jsonPath().getLong("id");
        var 신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");

        var 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        var 반포역 = 지하철역_생성_요청("반포역").jsonPath().getLong("id");
        var 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        var 사평역 = 지하철역_생성_요청("사평역").jsonPath().getLong("id");
        var 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");

        final int 고속터미널_반포_거리 = 2;
        final int 반포_논현_거리 = 2;
        final int 논현_신논현_거리 = 3;
        final int 고속터미널_신논현_거리 = 10;
        지하철_노선에_지하철_구간_생성_요청(칠호선, createSectionParams(고속터미널역, 반포역, 고속터미널_반포_거리));
        지하철_노선에_지하철_구간_생성_요청(칠호선, createSectionParams(반포역, 논현역, 반포_논현_거리));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(논현역, 신논현역, 논현_신논현_거리));
        지하철_노선에_지하철_구간_생성_요청(구호선, createSectionParams(고속터미널역, 신논현역, 고속터미널_신논현_거리));

        var response = 최단_경로_조회_요청(고속터미널역, 신논현역);

        final List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsSequence("고속터미널역", "반포역", "논현역", "신논현역");

        final Long distance = response.jsonPath().getLong("distance");
        assertThat(distance).isEqualTo(고속터미널_반포_거리 + 반포_논현_거리 + 논현_신논현_거리);
    }

    /**
     * when 존재하지 않는 출발역 ID와 도착역 ID로 지하철 경로조회 요청을 하면
     * then 경로조회에 실패한다
     */
    @DisplayName("최단 경로 조회 실패")
    @Test
    void failFindShortestPath() {
        // given
        var 칠호선 = 지하철_노선_생성_요청("7호선", "olive").jsonPath().getLong("id");
        var 구호선 = 지하철_노선_생성_요청("9호선", "gold").jsonPath().getLong("id");
        var 신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");

        var 고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");
        var 반포역 = 지하철역_생성_요청("반포역").jsonPath().getLong("id");
        var 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        var 사평역 = 지하철역_생성_요청("사평역").jsonPath().getLong("id");
        var 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");

        final int 고속터미널_반포_거리 = 2;
        final int 반포_논현_거리 = 2;
        final int 논현_신논현_거리 = 3;
        final int 고속터미널_신논현_거리 = 10;
        지하철_노선에_지하철_구간_생성_요청(칠호선, createSectionParams(고속터미널역, 반포역, 고속터미널_반포_거리));
        지하철_노선에_지하철_구간_생성_요청(칠호선, createSectionParams(반포역, 논현역, 반포_논현_거리));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionParams(논현역, 신논현역, 논현_신논현_거리));
        지하철_노선에_지하철_구간_생성_요청(구호선, createSectionParams(고속터미널역, 신논현역, 고속터미널_신논현_거리));

        // when & then
        var 존재하지않는_출발역 = 최단_경로_조회_요청(신논현역 + 1, 신논현역);
        assertThat(존재하지않는_출발역.statusCode()).isEqualTo(500);

        // when & // then
        var 존재하지않는_도착역 = 최단_경로_조회_요청(신논현역, 신논현역 + 1);
        assertThat(존재하지않는_도착역.statusCode()).isEqualTo(500);
    }

    private ExtractableResponse<Response> 최단_경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when().get(String.format("/paths?source=%d&target=%d", source, target))
                .then().log().all().extract();
    }


}
