package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationRequestBuilder.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

  private ExtractableResponse<Response> 강남역;
  private ExtractableResponse<Response> 역삼역;

  @BeforeEach
  void init() {
    강남역 = 지하철역_생성_요청("강남역");
    역삼역 = 지하철역_생성_요청("역삼역");
  }

  @DisplayName("지하철역을 생성한다.")
  @Test
  void createStation() {

    // then
    지하철역_생성됨(강남역);
  }

  @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
  @Test
  void createStationWithDuplicateName() {
    // given
    지하철역_생성됨(강남역);

    // when
    ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

    // then
    지하철역_생성실패됨(response);
  }

  @DisplayName("지하철역을 조회한다.")
  @Test
  void getStations() {
    /// given
    지하철역_생성됨(강남역);

    // when
    ExtractableResponse<Response> response = 지하철역_목록조회_요청();

    // then
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    List<Long> expectedLineIds = Arrays.asList(강남역, 역삼역).stream()
        .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
        .collect(Collectors.toList());
    List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
        .map(it -> it.getId())
        .collect(Collectors.toList());
    assertThat(resultLineIds).containsAll(expectedLineIds);
  }

  @DisplayName("지하철역을 제거한다.")
  @Test
  void deleteStation() {
    // given
    지하철역_생성됨(강남역);

    // when
    String uri = 강남역.header("Location");
    ExtractableResponse<Response> response = 지하철역_삭제_요청(uri);

    // then
    지하철역_삭제됨(response);
  }
}
