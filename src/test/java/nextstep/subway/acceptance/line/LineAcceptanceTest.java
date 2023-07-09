package nextstep.subway.acceptance.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import static nextstep.subway.acceptance.line.LineSteps.모든_지하철노선을_조회한다;
import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_생성한다;
import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_수정한다;
import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_제거한다;
import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_조회한다;
import static nextstep.subway.acceptance.station.StationSteps.지하철역을_생성한다;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.applicaion.line.request.LineCreateRequest;
import nextstep.subway.applicaion.line.request.LineUpdateRequest;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        final var request = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                지하철역을_생성한다("강남역").getId(),
                지하철역을_생성한다("양재역").getId(),
                10
        );

        // when
        지하철노선을_생성한다(request);

        // then
        assertThat(모든_지하철노선을_조회한다()).hasSize(1);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("모든 지하철노선 목록을 조회한다.")
    @Test
    void showLines() {
        final var requests = List.of(
                new LineCreateRequest(
                        "신분당선",
                        "bg-red-600",
                        지하철역을_생성한다("강남역").getId(),
                        지하철역을_생성한다("양재역").getId(),
                        10
                ),
                new LineCreateRequest(
                        "2호선",
                        "bg-green-600",
                        지하철역을_생성한다("강남역").getId(),
                        지하철역을_생성한다("역삼역").getId(),
                        10
                )
        );

        // given
        지하철노선을_생성한다(requests);

        // when
        final var responses = 모든_지하철노선을_조회한다();

        // then
        assertThat(responses).hasSize(requests.size());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findLine() {
        final var request = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                지하철역을_생성한다("강남역").getId(),
                지하철역을_생성한다("양재역").getId(),
                10
        );

        // given
        final Long lineId = 지하철노선을_생성한다(request).getId();

        // when
        final var response = 지하철노선을_조회한다(lineId);

        // then
        assertThat(response.getId()).isPositive();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        final var createRequest = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                지하철역을_생성한다("강남역").getId(),
                지하철역을_생성한다("양재역").getId(),
                10
        );
        final var updateRequest = new LineUpdateRequest("2호선", "bg-blue-123");

        // given
        final Long lineId = 지하철노선을_생성한다(createRequest).getId();

        // when
        지하철노선을_수정한다(lineId, updateRequest);

        // then
        final var response = 지하철노선을_조회한다(lineId);
        assertAll(
                () -> assertThat(response.getName()).isEqualTo(updateRequest.getName()),
                () -> assertThat(response.getColor()).isEqualTo(updateRequest.getColor())
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        final var request = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                지하철역을_생성한다("강남역").getId(),
                지하철역을_생성한다("양재역").getId(),
                10
        );

        // given
        final Long lineId = 지하철노선을_생성한다(request).getId();

        // when
        지하철노선을_제거한다(lineId);

        // then
        assertThat(모든_지하철노선을_조회한다()).isEmpty();
    }
}
