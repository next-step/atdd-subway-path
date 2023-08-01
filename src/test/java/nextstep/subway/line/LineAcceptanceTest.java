package nextstep.subway.line;

import common.AcceptanceTest;
import java.util.stream.Stream;
import nextstep.subway.station.StationTestStepDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.station.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static common.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능 인수 테스트")
@AcceptanceTest
public class LineAcceptanceTest {

    // When 지하철 노선을 생성하면
    // Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        var response = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, 빨강색600, 판교역, 광교역, 10);

        // then
        assertAll(
            () -> assertThat(response.getId()).isNotNull(),
            () -> assertThat(response.getName()).isEqualTo(신분당선),
            () -> assertThat(response.getColor()).isEqualTo(빨강색600),
            () -> assertThat(getStationNames(response)).containsExactly(판교역, 광교역)
        );
    }

    // Given 2개의 지하철 노선을 생성하고
    // When 지하철 노선 목록을 조회하면
    // Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLineList() {
        // given
        StationResponse someStationResponse = StationTestStepDefinition.지하철_역_생성_요청(판교역);
        StationResponse newStationResponse = StationTestStepDefinition.지하철_역_생성_요청(광교역);
        StationResponse anotherStationResponse = StationTestStepDefinition.지하철_역_생성_요청(양재역);

        var sinBundangLineResponse = LineTestStepDefinition.지하철_노선_생성_요청(
            신분당선,
            빨강색600,
            someStationResponse.getId(),
            newStationResponse.getId(),
            10);
        var bundangLineResponse = LineTestStepDefinition.지하철_노선_생성_요청(
            분당선,
            "bg-green-600",
            someStationResponse.getId(),
            anotherStationResponse.getId(),
            10);

        // when
        var response = LineTestStepDefinition.지하철_노선_목록_조회_요청();

        // then
        assertThat(response).containsExactly(sinBundangLineResponse, bundangLineResponse);
    }

    // Given 지하철 노선을 생성하고
    // When 생성한 지하철 노선을 조회하면
    // Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given & when
        var response = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, 빨강색600, 판교역, 광교역, 10);

        // then
        assertAll(
            () -> assertThat(response.getId()).isNotNull(),
            () -> assertThat(response.getName()).isEqualTo(신분당선),
            () -> assertThat(response.getColor()).isEqualTo(빨강색600),
            () -> assertThat(getStationNames(response)).containsExactly(판교역, 광교역)
        );
    }

    // Given 지하철 노선을 생성하고
    // When 생성한 지하철 노선을 수정하면
    // Then 해당 지하철 노선 정보는 수정된다
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        var lineResponse = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, 빨강색600, 판교역, 광교역, 10);

        // when
        LineTestStepDefinition.지하철_노선_수정_요청(lineResponse.getId(), 양재역, 빨강색600);

        // then
        var updateResponse = LineTestStepDefinition.지하철_노선_조회_요청(lineResponse.getId());
        assertAll(
            () -> assertThat(updateResponse.getName()).isEqualTo(양재역),
            () -> assertThat(updateResponse.getColor()).isEqualTo(빨강색600));
    }

    // Given 지하철 노선을 생성하고
    // When 생성한 지하철 노선을 삭제하면
    // Then 해당 지하철 노선 정보는 삭제된다
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        var lineResponse = LineTestStepDefinition.지하철_노선_생성_요청(신분당선, 빨강색600, 판교역, 광교역, 10);

        // when
        LineTestStepDefinition.지하철_노선_삭제_요청(lineResponse.getId());

        // then
        LineTestStepDefinition.없는_지하철_노선_조회_요청(lineResponse.getId());
    }

    private Stream<String> getStationNames(LineResponse response) {
        return response.getStations().stream().map(StationResponse::getName);
    }
}
