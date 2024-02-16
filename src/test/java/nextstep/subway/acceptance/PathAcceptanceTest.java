package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.section.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.utils.AcceptanceMethods.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 테스트")
public class PathAcceptanceTest extends AcceptanceTest {
    /**
     * given 환승역으로 이어진 노선을 3개 생성하고
     * when 출발역과 도착역 정보를 전달하면
     * then 출발여고가 도착역 사이의 최단 경로 정보를 응답한다.
     */
    @DisplayName("최단 경로 조회")
    @Test
    void shortestPath() {
        // given
        Long 교대역 = makeStation("교대역").jsonPath().getLong("id");
        Long 강남역 = makeStation("강남역").jsonPath().getLong("id");
        Long 양재역 = makeStation("양재역").jsonPath().getLong("id");
        Long 남부터미널역 = makeStation("남부터미널역").jsonPath().getLong("id");
        Long 이호선 = makeLine(new LineRequest("이호선", "green", 교대역, 강남역, 10L)).jsonPath().getLong("id");
        Long 신분당선 = makeLine(new LineRequest("신분당선", "red", 강남역, 양재역, 14L)).jsonPath().getLong("id");
        Long 삼호선 = makeLine(new LineRequest("삼호선", "orange", 양재역, 교대역, 23L)).jsonPath().getLong("id");
        makeSection(삼호선, new SectionRequest(양재역, 남부터미널역, 5L));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("source", 교대역)
                .param("target", 양재역)
                .when()
                .get("/paths")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getDouble("distance")).isEqualTo(23);
    }
}
