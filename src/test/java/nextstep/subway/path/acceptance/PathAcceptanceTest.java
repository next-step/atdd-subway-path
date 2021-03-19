package nextstep.subway.path.acceptance;

import static nextstep.subway.line.acceptance.LineAcceptanceTestSteps.구간등록요청;
import static nextstep.subway.line.acceptance.LineAcceptanceTestSteps.지하철_노선_생성요청;
import static nextstep.subway.path.acceptance.PathAcceptanceTestSteps.경로_조회됨;
import static nextstep.subway.path.acceptance.PathAcceptanceTestSteps.경로_조회실패됨;
import static nextstep.subway.path.acceptance.PathAcceptanceTestSteps.두역의_경로탐색을_요청한다;
import static nextstep.subway.station.StationAcceptanceTestSteps.지하철역_생성_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.acceptance.LineColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 탐색 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

  private long 신분당선;
  private long 이호선;
  private long 일호선;
  private long 양재역;
  private long 사당역;
  private long 강남역;
  private long 광교중앙역;
  private long 역삼역;
  private long 광명역;
  private long 금천구청역;
  private long 독산역;
  private long 철산역;

  @BeforeEach
  public void init() {
    강남역 = 지하철역_생성_요청("강남역").body().jsonPath().getLong("id");
    역삼역 = 지하철역_생성_요청("역삼역").body().jsonPath().getLong("id");
    사당역 = 지하철역_생성_요청("사당역").body().jsonPath().getLong("id");
    양재역 = 지하철역_생성_요청("양재역").body().jsonPath().getLong("id");
    광교중앙역 = 지하철역_생성_요청("광교중앙역").body().jsonPath().getLong("id");
    광명역 = 지하철역_생성_요청("광명역").body().jsonPath().getLong("id");
    금천구청역 = 지하철역_생성_요청("금천구청역").body().jsonPath().getLong("id");
    독산역 = 지하철역_생성_요청("독산역").body().jsonPath().getLong("id");
    철산역 = 지하철역_생성_요청("철산역").body().jsonPath().getLong("id");

    신분당선 = 지하철_노선_생성요청("신분당선", LineColor.RED, 광교중앙역, 양재역).body().jsonPath().getLong("id");
    구간등록요청(신분당선, 광교중앙역, 양재역, 5);
    구간등록요청(신분당선, 양재역, 강남역, 5);

    이호선 = 지하철_노선_생성요청("이호선", LineColor.GREEN, 사당역, 강남역).body().jsonPath().getLong("id");
    구간등록요청(이호선, 사당역, 강남역, 5);
    구간등록요청(이호선, 강남역, 역삼역, 5);

    일호선 = 지하철_노선_생성요청("일호선", LineColor.BLUE, 광명역, 독산역).body().jsonPath().getLong("id");
    구간등록요청(이호선, 독산역, 금천구청역, 5);
    구간등록요청(이호선, 금천구청역, 독산역, 5);
  }

  @DisplayName("두 역간의 경로를 탐색한다")
  @Test
  void findPath() {
    //when
    ExtractableResponse<Response> 양재역_역삼역_경로_응답 = 두역의_경로탐색을_요청한다(광교중앙역, 역삼역);
    //then
    경로_조회됨(양재역_역삼역_경로_응답);
  }

  @DisplayName("두 역간의 경로를 조회할때 출발역과 도착역이 같으면 탐색하지 못한다.")
  @Test
  void findPathWithSameStation() {
    //when
    ExtractableResponse<Response> 양재역_역삼역_경로_응답 = 두역의_경로탐색을_요청한다(양재역, 양재역);
    //then
    경로_조회실패됨(양재역_역삼역_경로_응답);
  }

  @DisplayName("두 역간의 경로를 조회할때 출발역과 도착역이 연결되어 있지 않으면 탐색하지 못한다.")
  @Test
  void findPathWithNotConnectedStation() {
    //when
    ExtractableResponse<Response> 양재역_역삼역_경로_응답 = 두역의_경로탐색을_요청한다(양재역, 광명역);
    //then
    경로_조회실패됨(양재역_역삼역_경로_응답);
  }

  @DisplayName("두 역간의 경로를 조회할때 출발역이나 도착역이 노선에 등록되어 있지않으면 탐색하지 못한다.")
  @Test
  void findPathWithUnregisteredStation() {
    //when
    ExtractableResponse<Response> 양재역_역삼역_경로_응답 = 두역의_경로탐색을_요청한다(양재역, 철산역);
    //then
    경로_조회실패됨(양재역_역삼역_경로_응답);
  }
}
