package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineAcceptanceTest.노선_만들기;
import static nextstep.subway.acceptance.SectionAcceptanceTest.구간_추가;
import static nextstep.subway.acceptance.StationAcceptanceTest.역_만들기;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("지하철 경로조회 관련 기능")
@Sql("/teardown.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {
  @LocalServerPort
  int port;
  private String 교대역;
  private String 강남역;
  private String 양재역;
  private String 남부터미널역;
  private String 고속터미널역;
  private String 신사역;
  private String 서울역;
  private String 부산역;
  private String 이호선;
  private String 신분당선;
  private String 삼호선;

  private String 사호선;

  /**
   * 교대역    --- *2호선* ---   강남역
   * |                        |
   * *3호선*                   *신분당선*
   * |                        |
   * 남부터미널역  --- *3호선* --- 양재역
   *
   *
   * 고속 터미널역 --- *4호선* --- 신사역
   */
  @BeforeEach
  public void setUp() {
    RestAssured.port = port;

    교대역 = 역_만들기("교대역").jsonPath().getString("id");
    강남역 = 역_만들기("강남역").jsonPath().getString("id");
    양재역 = 역_만들기("양재역").jsonPath().getString("id");
    남부터미널역 = 역_만들기("남부터미널역").jsonPath().getString("id");
    고속터미널역 = 역_만들기("고속터미널역").jsonPath().getString("id");
    신사역 = 역_만들기("신사역").jsonPath().getString("id");

    서울역 = 역_만들기("서울역").jsonPath().getString("id");
    부산역 = 역_만들기("부산역").jsonPath().getString("id");

    이호선 = 노선_만들기("2호선", "green", 교대역, 강남역, "10").jsonPath().getString("id");
    신분당선 = 노선_만들기("신분당선", "red", 강남역, 양재역, "10").jsonPath().getString("id");
    삼호선 = 노선_만들기("3호선", "orange", 교대역, 남부터미널역, "2").jsonPath().getString("id");
    사호선 = 노선_만들기("4호선", "blue", 고속터미널역, 신사역, "2").jsonPath().getString("id");
    구간_추가(삼호선, 남부터미널역, 양재역, "3");
  }

  /**
   * Given 노선이 3개 주어지고 노선이 서로 연결되었을때,
   * When 교대역에서 양재역까지 최소 거리를 구할 때,
   * Then 교대역 -> 남부터미널역 -> 양재역 으로 총 12 의 거리를 최소 경로로 조회한다.
   */
  @DisplayName("최소 거리의 경로를 조회한다.")
  @Test
  void findShortestRoute() {
    // When
    ExtractableResponse<Response> response = 경로_조회(교대역, 양재역);

    // Then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    // THEN
    경로_조회_구간_검증(response, "교대역", "남부터미널역", "양재역");
    경로_조회_거리_검증(response, 5L );
  }

  /**
   * Given 노선이 3개 주어지고 노선이 서로 연결되었을때,
   * When 교대역에서 교대역까지 최소 거리를 구할 때,
   * Then 빈 경로로 최소 경로로 조회에 실패한다.
   */
  @DisplayName("오류 케이스: 출발역과 도착역이 같은 경우 빈 경로로 실패한다.")
  @Test
  void sourceTargetSameError() {
    // When
    ExtractableResponse<Response> response = 경로_조회(교대역, 교대역);

    // Then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  /**
   * Given 노선이 4개 주어지고 4호선은 연결되어 있지 않을때,
   * When 교대역에서 신사역까지 최소 거리를 구할 때,
   * Then 연결되어 있지 않아 조회에 실패한다.
   */
  @DisplayName("오류 케이스: 출발역과 도착역이 연결되어 있지 않아 경로 조회에 실패한다.")
  @Test
  void notConnectedError() {
    // When
    ExtractableResponse<Response> response = 경로_조회(교대역, 신사역);

    // Then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

  }

  /**
   * Given 노선이 3개 주어지고 노선이 서로 연결되었을때,
   * When 서울역에서 부산역까지 최소 거리를 구할 때,
   * Then 존재하지 않는 역으로 경로 조회가 실패한다.
   */
  @DisplayName("오류 케이스: 출발역과 도착역이 존재하지 않아 경로 조회에 실패한다.")
  @Test
  void notExistingStationsError() {
    // When
    ExtractableResponse<Response> response = 경로_조회(서울역, 부산역);

    // Then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  private static ExtractableResponse<Response> 경로_조회(String source, String target) {

    ExtractableResponse<Response> response =
        RestAssured.given().log().all()
            .queryParam("source", source)
            .queryParam("target", target)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/paths")
            .then().log().all()
            .extract();
    return response;
  }

  private void 경로_조회_구간_검증(ExtractableResponse<Response> response, String... values) {
    assertThat(response.jsonPath().getList("stations.name")).containsExactly(values);
  }

  private void 경로_조회_거리_검증(ExtractableResponse<Response> response, Long distance) {
    assertThat(response.jsonPath().getLong("distance")).isEqualTo(distance);
  }

}
