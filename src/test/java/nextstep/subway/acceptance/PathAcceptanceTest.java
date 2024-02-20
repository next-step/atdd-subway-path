package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

  static LineResponse 이호선;
  static LineResponse 신분당선;
  static LineResponse 분당선;
  static LineResponse 삼호선;
  static LineResponse 부산선;

  static StationResponse 강남역;
  static StationResponse 역삼역;
  static StationResponse 선릉역;
  static StationResponse 한티역;
  static StationResponse 양재역;
  static StationResponse 매봉역;
  static StationResponse 도곡역;

  static StationResponse 남포역;
  static StationResponse 서면역;

  /**
   * Given 여러 지하철 노선을 만든다.
   *                         3             1
   *                 강남 --------- 역삼 --------- 선릉
   *                  |                          |
   *                  |                          |   1
   *              1   |                         한티
   *                  |                          |
   *                  |                          |   1
   *                 양재 --------- 매봉 --------- 도곡
   *                         1            1
   *
   *                 남포 --------- 서면
   *                         5
   */
  @BeforeEach
  public void setUp() {
    super.setUp();

    강남역 = 지하철역_생성("강남역");
    역삼역 = 지하철역_생성("역삼역");
    선릉역 = 지하철역_생성("선릉역");
    한티역 = 지하철역_생성("한티역");
    양재역 = 지하철역_생성("양재역");
    매봉역 = 지하철역_생성("매봉역");
    도곡역 = 지하철역_생성("도곡역");
    남포역 = 지하철역_생성("남포역");
    서면역 = 지하철역_생성("서면역");

    이호선 = 지하철_노선_생성("2호선", "초록", 강남역.getId(), 역삼역.getId(), 3);
    지하철_구간_생성_요청(이호선.getId(), 역삼역.getId(), 선릉역.getId(), 1);

    신분당선 = 지하철_노선_생성("신분당선", "빨강", 강남역.getId(), 양재역.getId(), 1);

    분당선 = 지하철_노선_생성("분당선", "노랑", 선릉역.getId(), 한티역.getId(), 1);
    지하철_구간_생성_요청(분당선.getId(), 한티역.getId(), 도곡역.getId(), 1);

    삼호선 = 지하철_노선_생성("3호선", "주황", 양재역.getId(), 매봉역.getId(), 1);
    지하철_구간_생성_요청(삼호선.getId(), 매봉역.getId(), 도곡역.getId(), 1);

    부산선 = 지하철_노선_생성("부산선", "검정", 남포역.getId(), 서면역.getId(), 5);
  }

  /**
   * when 매봉역에서 역삼역으로 가는 경로를 탐색하면
   * then 더 짧은 거리인 분당선을 지나는 코스를 반환한다.
   */
  @DisplayName("최단 경로 탐색 성공")
  @Test
  void 경로_탐색_성공() {
    // when
    final var response = 경로_탐색_요청(매봉역.getId(), 역삼역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(
        매봉역.getId(),
        도곡역.getId(),
        한티역.getId(),
        선릉역.getId(),
        역삼역.getId()
    );
    assertThat(response.jsonPath().getInt("distance")).isEqualTo(4);
  }

  @DisplayName("출발역 정보 누락")
  @Test
  void 출발역_정보_누락() {
    // when
    final var response = 경로_탐색_요청(null, 역삼역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.body().asString()).contains("출발역 정보를 입력해주세요.");
  }

  @DisplayName("도착역 정보 누락")
  @Test
  void 도착역_정보_누락() {
    // when
    final var response = 경로_탐색_요청(매봉역.getId(), null);

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.body().asString()).contains("도착역 정보를 입력해주세요.");
  }

  @DisplayName("경로 탐색 실패")
  @Test
  void 경로를_탐색할_수_없음() {
    // when
    final var response = 경로_탐색_요청(강남역.getId(), 서면역.getId());

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.body().asString()).isEqualTo("경로를 찾을 수 없습니다.");
  }

  private ExtractableResponse<Response> 경로_탐색_요청(Long source, Long target) {
    return RestAssured.given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .queryParam("source", source)
        .queryParam("target", target)
        .when().get("/paths")
        .then().log().all().extract();
  }
}
