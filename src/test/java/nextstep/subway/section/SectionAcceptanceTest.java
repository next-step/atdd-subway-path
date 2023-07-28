package nextstep.subway.section;

import static common.Constants.강남역;
import static common.Constants.교대역;
import static common.Constants.남부터미널역;
import static common.Constants.삼호선;
import static common.Constants.양재역;
import static common.Constants.빨강색600;
import static common.Constants.광교역;
import static common.Constants.신논현역;
import static common.Constants.신분당선;
import static common.Constants.이호선;
import static common.Constants.주황색600;
import static common.Constants.파랑색600;
import static common.Constants.판교역;
import static nextstep.subway.line.LineTestStepDefinition.지하철_노선_생성_요청;
import static nextstep.subway.line.LineTestStepDefinition.지하철_노선_조회_요청;
import static nextstep.subway.section.SectionTestStepDefinition.*;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.station.StationTestStepDefinition.지하철_역_생성_요청;

import common.AcceptanceTest;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.line.LineResponse;

@DisplayName("지하철 구간 관련 기능 인수 테스트")
@AcceptanceTest
public class SectionAcceptanceTest {

    private Long 판교역정보;
    private Long 광교역정보;
    private Long 양재역정보;

    @BeforeEach
    public void init() {
        판교역정보 = 지하철_역_생성_요청(판교역).getId();
        광교역정보 = 지하철_역_생성_요청(광교역).getId();
        양재역정보 = 지하철_역_생성_요청(양재역).getId();
    }

    // Given 지하철 노선을 생성하고
    // When 지하철 노선 구간 사이에 존재하는 구간을 추가하면
    // Then 지하철 노선 조회시 추가한 구간이 중간에 포함되어있다.
    @DisplayName("지하철 노선 사이에 존재하는 구간을 추가한다.")
    @Test
    void createSection_pass_insert() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);

        지하철_구간_생성_요청(신분당선응답.getId(), 판교역정보, 양재역정보, 5);

        var 노선조회응답 = 지하철_노선_조회_요청(신분당선응답.getId());
        assertThat(역이름_목록(노선조회응답)).containsExactly(판교역, 양재역, 광교역);
    }

    // Given 지하철 노선을 생성하고
    // When 지하철 노선 상행 종점역을 하행역으로 가지는 구간을 추가하면
    // Then 지하철 노선 조회시 추가한 구간이 맨 앞에 포함되어있다.
    @DisplayName("지하철 노선 사이에 존재하는 구간을 추가한다.")
    @Test
    void createSection_pass_addHead() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);

        지하철_구간_생성_요청(신분당선응답.getId(), 양재역정보, 판교역정보, 5);

        var 노선조회응답 = 지하철_노선_조회_요청(신분당선응답.getId());
        assertThat(역이름_목록(노선조회응답)).containsExactly(양재역, 판교역, 광교역);
    }

    // Given 지하철 노선을 생성하고
    // When 지하철 노선 하행 종점역을 상행역으로 가지는 구간을 추가하면
    // Then 지하철 노선 조회시 추가한 구간이 맨 뒤에 포함되어있다.
    @DisplayName("지하철 노선 사이에 존재하는 구간을 추가한다.")
    @Test
    void createSection_pass_addTail() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);

        지하철_구간_생성_요청(신분당선응답.getId(), 광교역정보, 양재역정보, 5);

        var 노선조회응답 = 지하철_노선_조회_요청(신분당선응답.getId());
        assertThat(역이름_목록(노선조회응답)).containsExactly(판교역, 광교역, 양재역);
    }

    // Given 지하철 노선을 생성하고
    // When 기존 구간과 거리가 같은 구간을 추가하면
    // Then 구간 추가에 실패한다
    @DisplayName("지하철 노선에 기존 구간과 거리가 같거나 큰 구간을 역 사이에 추가하면 실패한다.")
    @Test
    void createSection_fail_sectionDistanceIsSame() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);

        var 상태코드 = 지하철_구간_생성_요청_상태_코드_반환(신분당선응답.getId(), 판교역정보, 양재역정보, 10);

        assertThat(상태코드).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // Given 지하철 노선을 생성하고
    // When 기존 구간의 상행역과 하행역이 같은 구간을 추가하면
    // Then 구간 추가에 실패한다
    @DisplayName("이미 등록된 상행역과 하행역을 가진 구간을 추가하면 실패한다")
    @Test
    void createSection_fail_sectionAlreadyExist() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);

        var 상태코드 = 지하철_구간_생성_요청_상태_코드_반환(신분당선응답.getId(), 판교역정보, 광교역정보, 10);

        assertThat(상태코드).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // Given 지하철 노선을 생성하고
    // When 상행역과 하행역 모두 등록되지 않은 구간을 추가하면
    // Then 구간 추가에 실패한다
    @DisplayName("상행역과 하행역 모두 등록되지 않은 구간을 추가하면 실패한다")
    @Test
    void createSection_fail_sectionNoIntersection() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);

        var 상태코드 = 지하철_구간_생성_요청_상태_코드_반환(신분당선응답.getId(), 신논현역, 양재역, 10);

        assertThat(상태코드).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // Given 지하철 노선에 구간을 추가하고
    // When 하행 종점역이 존재하는 구간을 제거 요청하면
    // Then 지하철 노선 조회시 추가한 구간이 제거된 것을 확인할 수 있다.
    @DisplayName("지하철 노선의 하행 종점역에 대한 구간을 제거한다.")
    @Test
    void deleteSection() {
        var 신분당선응답 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역, 광교역, 10);
        지하철_구간_생성_요청(신분당선응답.getId(), 하행종점역(신분당선응답), 양재역정보, 10);

        지하철_구간_제거_요청(신분당선응답.getId(), 양재역정보);

        var 신분당선_조회 = 지하철_노선_조회_요청(신분당선응답.getId());
        assertThat(역이름_목록(신분당선_조회)).containsExactly(판교역, 광교역);
    }

    // Given 지하철 노선에 구간을 생성하고
    // When 상행 요청역에 대해 삭제 요청하면
    // Then 지하철 노선 조회시 상행 종점역을 발견할 수 없다
    @DisplayName("지하철 노선의 상행 종점역에 대한 구간을 제거한다.")
    @Test
    void deleteSection_deleteUpEnd() {
        var 신분당선_생성 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);
        지하철_구간_생성_요청(신분당선_생성.getId(), 광교역정보, 양재역정보, 10);

        지하철_구간_제거_요청(신분당선_생성.getId(), 판교역정보);

        var 노선_조회 = 지하철_노선_조회_요청(신분당선_생성.getId());
        assertThat(역이름_목록(노선_조회)).containsExactly(광교역, 양재역);
    }

    // Given 지하철 노선에 구간을 생성하고
    // When 중간 역에 대한 삭제 요청하면
    // Then 지하철 노선 조회시 삭제한 구간이 제거되고, 양 옆의 구간이 연결되어 있는 것을 확인할 수 있다.
    @DisplayName("지하철 구간의 중간 역에 대해 제거한다")
    @Test
    void deleteSection_deleteMiddle() {
        var 신분당선_생성 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역정보, 광교역정보, 10);
        지하철_구간_생성_요청(신분당선_생성.getId(), 광교역정보, 양재역정보, 10);

        지하철_구간_제거_요청(신분당선_생성.getId(), 광교역정보);

        var 신분당선_조회 = 지하철_노선_조회_요청(신분당선_생성.getId());
        assertThat(역이름_목록(신분당선_조회)).containsExactly(판교역, 양재역);
    }

    // Given 지하철 노선을 생성하고
    // When 상행 종점역과 하행 종점역만 있는 노선에 구간 삭제 요청하면
    // Then 지하철 노선 조회시 추가한 구역이 제거되지 않은 것을 확인할 수 있다.
    @DisplayName("지하철 노선에 구간 제거시 상행 종점역과 하행 종점역만 있는 노선에 구간이라면 실패한다.")
    @Test
    void deleteSection_fail_sectionOnlyExistsUpAndDownEndStationInLine() {
        var 신분당선_생성 = 지하철_노선_생성_요청(신분당선, 빨강색600, 판교역, 광교역, 10);

        var 상태코드 = 지하철_구간_제거_요청_상태_코드_반환(신분당선_생성.getId(), 상행종점역(신분당선_생성));

        var 신분당선_조회 = 지하철_노선_조회_요청(신분당선_생성.getId());
        assertThat(상태코드).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(역이름_목록(신분당선_조회)).containsExactly(판교역, 광교역);
    }

    // Given 지하철 노선과 구간을 추가하고
    // When 경로 조회 요청하면
    // Then 가장 가까운 경로의 역 목록과 조회한 경로 구간의 거리를 확인할 수 있다
    @DisplayName("지하철 경로 탐색")
    void route() {
        // given
        var 남부터미널역정보 = 지하철_역_생성_요청(남부터미널역).getId();
        var 강남역정보 = 지하철_역_생성_요청(강남역).getId();
        var 교대역정보 = 지하철_역_생성_요청(교대역).getId();

        지하철_노선_생성_요청(신분당선, 빨강색600, 강남역정보, 양재역정보, 2);
        지하철_노선_생성_요청(이호선, 파랑색600, 교대역정보, 강남역정보, 3);
        var 삼호선정보 = 지하철_노선_생성_요청(삼호선, 주황색600, 교대역정보, 남부터미널역정보, 10).getId();

        지하철_구간_생성_요청(삼호선정보, 남부터미널역정보, 양재역정보, 10);

        // when
        var 양재역_교대역_경로_조회 = 지하철_경로_조회(양재역정보, 교대역정보);

        // then
        assertThat(역이름_목록(양재역_교대역_경로_조회)).containsExactly(양재역, 강남역, 교대역);
        assertThat(양재역_교대역_경로_조회.getDistance()).isEqualTo(5);
    }

    private Stream<String> 역이름_목록(PathResponse response) {
        return response.getStations().stream().map(StationResponse::getName);
    }

    private Stream<String> 역이름_목록(LineResponse response) {
        return response.getStations().stream().map(StationResponse::getName);
    }

    private Long 하행종점역(LineResponse response) {
        List<StationResponse> stations = response.getStations();
        return stations.get(stations.size() - 1).getId();
    }

    private Long 상행종점역(LineResponse response) {
        return response.getStations().get(0).getId();
    }
}
