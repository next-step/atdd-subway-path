package nextstep.subway.section;

import static common.Constants.또다른지하철역;
import static common.Constants.새로운지하철역;
import static common.Constants.신분당선;
import static common.Constants.지하철역;
import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.line.LineTestStepDefinition.지하철_노선_생성_요청;
import static nextstep.subway.station.StationTestStepDefinition.지하철_역_생성_요청;

import common.AcceptanceTest;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.line.LineTestStepDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.line.LineResponse;
import nextstep.subway.station.Station;

@DisplayName("지하철 구간 관련 기능")
@AcceptanceTest
public class SectionAcceptanceTest {

    // Given 지하철 노선을 생성하고
    // When 지하철 노선에 구간을 추가하면
    // Then 지하철 노선 조회시 추가한 구간이 포함되어있다.
    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void createSection() {
        // given
        var lineCreateResponse = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, "bg-red-600", 지하철역, 새로운지하철역, 10);
        var stationResponse = 지하철_역_생성_요청(또다른지하철역);

        // when
        SectionTestStepDefinition.지하철_구간_생성_요청(lineCreateResponse.getId(), getDownEndStationId(lineCreateResponse),
            stationResponse.getId(), 10);

        // then
        var lineResponse = LineTestStepDefinition.지하철_노선_조회_요청(lineCreateResponse.getId());
        assertThat(getStationNames(lineResponse)).containsExactly(지하철역, 새로운지하철역, 또다른지하철역);
    }

    // Given 지하철 노선에 구간을 추가하고
    // When 추가한 구간을 제거 요청하면
    // Then 지하철 노선 조회시 추가한 구간이 제거된 것을 확인할 수 있다.
    @DisplayName("지하철 노선에 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        var lineCreateResponse = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, "bg-red-600", 지하철역, 새로운지하철역, 10);
        var stationResponse = 지하철_역_생성_요청(또다른지하철역);
        SectionTestStepDefinition.지하철_구간_생성_요청(lineCreateResponse.getId(), getDownEndStationId(lineCreateResponse),
            stationResponse.getId(), 10);

        // when
        SectionTestStepDefinition.지하철_구간_제거_요청(lineCreateResponse.getId(), stationResponse.getId());

        // then
        var lineResponse = LineTestStepDefinition.지하철_노선_조회_요청(lineCreateResponse.getId());
        assertThat(getStationNames(lineResponse)).containsExactly(지하철역, 새로운지하철역);
    }

    // Given 지하철 노선에 구간을 추가하고
    // When 하행 종점역이 아닌 역을 제거 요청하면
    // Then 지하철 노선 조회시 추가한 구역이 제거되지 않은 것을 확인할 수 있다.
    @DisplayName("지하철 노선에 구간 제거시 하행 종점역이 아닌 역을 제거 요청하면 실패한다.")
    @Test
    void deleteSection_fail_sectionDoesNotMatchWithDownEndStationOfLine() {
        // given
        var lineCreateResponse = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, "bg-red-600", 지하철역, 새로운지하철역, 10);
        var stationResponse = 지하철_역_생성_요청(또다른지하철역);
        SectionTestStepDefinition.지하철_구간_생성_요청(lineCreateResponse.getId(), getDownEndStationId(lineCreateResponse),
            stationResponse.getId(), 10);

        // when
        var statusCode = SectionTestStepDefinition.지하철_구간_제거_요청_상태_코드_반환(lineCreateResponse.getId(),
            getUpEndStationId(lineCreateResponse));

        // then
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
        var lineResponse = LineTestStepDefinition.지하철_노선_조회_요청(lineCreateResponse.getId());
        assertThat(getStationNames(lineResponse)).containsExactly(지하철역, 새로운지하철역, 또다른지하철역);
    }

    // Given 지하철 노선을 생성하고
    // When 상행 종점역과 하행 종점역만 있는 노선에 구간 삭제 요청하면
    // Then 지하철 노선 조회시 추가한 구역이 제거되지 않은 것을 확인할 수 있다.
    @DisplayName("지하철 노선에 구간 제거시 상행 종점역과 하행 종점역만 있는 노선에 구간이라면 실패한다.")
    @Test
    void deleteSection_fail_sectionOnlyExistsUpAndDownEndStationInLine() {
        // given
        var lineCreateResponse = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, "bg-red-600", 지하철역, 새로운지하철역, 10);

        // when
        var statusCode = SectionTestStepDefinition.지하철_구간_제거_요청_상태_코드_반환(lineCreateResponse.getId(),
            getUpEndStationId(lineCreateResponse));

        // then
        var lineResponse = LineTestStepDefinition.지하철_노선_조회_요청(lineCreateResponse.getId());
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStationNames(lineResponse)).containsExactly(지하철역, 새로운지하철역);
    }

    private Stream<String> getStationNames(LineResponse response) {
        return response.getStations().stream().map(Station::getName);
    }

    private Long getDownEndStationId(LineResponse response) {
        List<Station> stations = response.getStations();
        return stations.get(stations.size() - 1).getId();
    }

    private Long getUpEndStationId(LineResponse response) {
        return response.getStations().get(0).getId();
    }
}
