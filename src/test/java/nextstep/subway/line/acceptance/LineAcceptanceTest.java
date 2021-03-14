package nextstep.subway.line.acceptance;

import static nextstep.subway.line.acceptance.LineRequestBuilder.createLineRequestParams;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선_생성됨;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선_생성요청;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선_수정됨;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선_조회됨;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선목록_조회됨;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선목록조회_요청;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선삭제_요청;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선삭제됨;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선생성_실패됨;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선수정_요청;
import static nextstep.subway.line.acceptance.LineRequestBuilder.지하철_노선조회_요청;
import static nextstep.subway.station.StationRequestBuilder.지하철역_생성_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

  private long startStationId;
  private long endStationId;
  private long lineId;
  private ExtractableResponse<Response> createLineResponse;

  @BeforeEach
  public void init() {
    startStationId = 지하철역_생성_요청("광교역").body().jsonPath().getLong("id");
    endStationId = 지하철역_생성_요청("강남역").body().jsonPath().getLong("id");
    createLineResponse = 지하철_노선_생성요청("신분당선", LineColor.RED, startStationId, endStationId);
    lineId = createLineResponse.body().jsonPath().getLong("id");
  }


  @DisplayName("지하철 노선을 생성한다.")
  @Test
  void createLine() {
    //then
    지하철_노선_생성됨(createLineResponse);
  }

  @DisplayName("이미 등록된 노선을 생성한다.")
  @Test
  void createLineWithDuplicateName() {
    //given
    지하철_노선_생성됨(createLineResponse);
    //when
    ExtractableResponse<Response> response = 지하철_노선_생성요청("신분당선", LineColor.RED, startStationId,
        endStationId);
    //then
    지하철_노선생성_실패됨(response);
  }

  @DisplayName("지하철 노선 목록을 조회한다.")
  @Test
  void getLines() {
    //given
    지하철_노선_생성됨(createLineResponse);
    //when
    ExtractableResponse<Response> response = 지하철_노선목록조회_요청();
    //then
    지하철_노선목록_조회됨(response, "신분당선");
  }

  @DisplayName("지하철 노선을 조회한다.")
  @Test
  void getLine() {
    //given
    지하철_노선_생성됨(createLineResponse);
    //when
    ExtractableResponse<Response> response = 지하철_노선조회_요청(lineId);
    //then
    지하철_노선_조회됨(response);
  }

  @DisplayName("지하철 노선을 수정한다.")
  @Test
  void updateLine() {
    //given
    지하철_노선_생성됨(createLineResponse);
    //when
    ExtractableResponse<Response> response = 지하철_노선수정_요청(
        createLineRequestParams("신분당선", LineColor.YELLOW, startStationId, endStationId), lineId);
    //then
    지하철_노선_수정됨(response, LineColor.YELLOW);
  }

  @DisplayName("지하철 노선을 제거한다.")
  @Test
  void deleteLine() {
    //given
    지하철_노선_생성됨(createLineResponse);
    //when
    ExtractableResponse<Response> response = 지하철_노선삭제_요청(lineId);
    //then
    지하철_노선삭제됨(response);

  }
}
