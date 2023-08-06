package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.station.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest {

    /**
     * GIVEN 출발역과 도착역이 같은 라인에 속행있으며, 가는 길은 하나이다.
     * WHEN 출발역과 도착역을 입력받는다.
     * THEN 최단 거리를 구한다.
     */
    @Test
    @DisplayName("한개의 길에서 출발역에서 도착역까지 최단 거리를 구한다.")
    void findPath() {
        // given
        StationResponse 서현역 = StationTestHelper.createStation("서현역");
        StationResponse 이매역 = StationTestHelper.createStation("이매역");
        StationResponse 야탑역 = StationTestHelper.createStation("야탑역");
        LineResponse 분당선 = LineTestHelper.createLine("분당선");
        LineTestHelper.createSection(분당선.getId(), 서현역.getId(), 이매역.getId(), 5);
        LineTestHelper.createSection(분당선.getId(), 이매역.getId(), 야탑역.getId(), 10);

        // when
        ExtractableResponse<Response> pathResponse = PathTestHelper.findPath(서현역.getId(), 야탑역.getId());

        // then
        assertThat(pathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(pathResponse.body().jsonPath().<Long>get("distance")).isEqualTo(15L);
    }

    @Test
    @DisplayName("두개의 길에서 출발역에서 도착역까지 최단 거리를 구한다.")
    void findPath2() {
        // given
        StationResponse 서현역 = StationTestHelper.createStation("서현역");
        StationResponse 이매역 = StationTestHelper.createStation("이매역");
        StationResponse 야탑역 = StationTestHelper.createStation("야탑역");
        StationResponse 수내역 = StationTestHelper.createStation("수내역");
        LineResponse 분당선 = LineTestHelper.createLine("분당선");
        LineTestHelper.createSection(분당선.getId(), 서현역.getId(), 이매역.getId(), 5);
        LineTestHelper.createSection(분당선.getId(), 이매역.getId(), 야탑역.getId(), 10);
        LineResponse 신분당선 = LineTestHelper.createLine("신분당선");
        LineTestHelper.createSection(신분당선.getId(), 서현역.getId(), 수내역.getId(), 3);
        LineTestHelper.createSection(신분당선.getId(), 수내역.getId(), 야탑역.getId(), 7);

        // when
        ExtractableResponse<Response> pathResponse = PathTestHelper.findPath(서현역.getId(), 야탑역.getId());

        // then
        assertThat(pathResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(pathResponse.body().jsonPath().<Long>get("distance")).isEqualTo(10L);
    }
}
