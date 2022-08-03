package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    @Autowired
    private PathAcceptanceFixture pathAcceptanceFixture;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        pathAcceptanceFixture.지하철_노선이_구성된다();
    }

    /**
     * when 출발역 ID와 도착역 ID로 지하철 경로조회 요청을 하면
     * then 거리의 합이 가장 작은 경로를 구할 수 있다
     */
    @DisplayName("최단 경로 경로 조회")
    @Test
    void findShortestPath() {
        var response = 최단_경로_조회_요청(pathAcceptanceFixture.고속터미널역, pathAcceptanceFixture.신논현역);

        final List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsSequence("고속터미널역", "반포역", "논현역", "신논현역");

        final Long distance = response.jsonPath().getLong("distance");
        assertThat(distance).isEqualTo(pathAcceptanceFixture.고속터미널_반포_거리 + pathAcceptanceFixture.반포_논현_거리 + pathAcceptanceFixture.논현_신논현_거리);
    }

    /**
     * when 존재하지 않는 출발역 ID와 도착역 ID로 지하철 경로조회 요청을 하면
     * then 경로조회에 실패한다
     */
    @DisplayName("최단 경로 조회 실패")
    @Test
    void failFindShortestPath() {
        // when & then
        var 존재하지않는_출발역 = 최단_경로_조회_요청(pathAcceptanceFixture.신논현역 + 1, pathAcceptanceFixture.신논현역);
        assertThat(존재하지않는_출발역.statusCode()).isEqualTo(500);

        // when & // then
        var 존재하지않는_도착역 = 최단_경로_조회_요청(pathAcceptanceFixture.신논현역, pathAcceptanceFixture.신논현역 + 1);
        assertThat(존재하지않는_도착역.statusCode()).isEqualTo(500);
    }

    private ExtractableResponse<Response> 최단_경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when().get(String.format("/paths?source=%d&target=%d", source, target))
                .then().log().all().extract();
    }

}
