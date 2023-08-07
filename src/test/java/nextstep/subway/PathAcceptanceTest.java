package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.common.DependentTest;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 테스트")
@DependentTest
@Sql(scripts = {"/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
        assertThat(pathResponse.body().as(PathResponse.class).getDistance()).isEqualTo(15L);
        assertThat(pathResponse.body().as(PathResponse.class).getStations())
            .extracting(StationResponse::getName)
            .containsExactly("서현역", "이매역", "야탑역");
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
        assertThat(pathResponse.body().as(PathResponse.class).getDistance()).isEqualTo(10L);
    }

    /**
     * GIVEN 출발역과 도착역이 같은 라인에 속행있으며, 가는 길은 하나이다.
     * WHEN 출발역과 도착역을 동일하게 검색한다.
     * THEN 예외가 발생한다
     */
    @Test
    @DisplayName("출발역과 도착역이 같은 경우 예외를 발생시킨다.")
    void exceptionTest1() {
        // given
        StationResponse 서현역 = StationTestHelper.createStation("서현역");
        StationResponse 이매역 = StationTestHelper.createStation("이매역");
        LineResponse 분당선 = LineTestHelper.createLine("분당선");
        LineTestHelper.createSection(분당선.getId(), 서현역.getId(), 이매역.getId(), 5);

        // when
        ExtractableResponse<Response> pathResponse = PathTestHelper.findPath(서현역.getId(), 서현역.getId());

        // then
        assertThat(pathResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * GIVEN 출발역과 도착역이 다른 라인에 속해있으며, 가는 길은 없다
     * WHEN 출발역과 도착역을 입력받는다.
     * THEN 예외가 발생한다
     */
    @Test
    @DisplayName("출발역과 도착역이 연결되어있지 않은 경우 예외를 발생시킨다.")
    void exceptionTest2() {
        // given
        StationResponse 서현역 = StationTestHelper.createStation("서현역");
        StationResponse 이매역 = StationTestHelper.createStation("이매역");
        StationResponse 야탑역 = StationTestHelper.createStation("야탑역");
        LineResponse 분당선 = LineTestHelper.createLine("분당선");
        LineTestHelper.createSection(분당선.getId(), 서현역.getId(), 이매역.getId(), 5);

        // when
        ExtractableResponse<Response> pathResponse = PathTestHelper.findPath(서현역.getId(), 야탑역.getId());

        // then
        assertThat(pathResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * GIVEN 출발역, 도착역이 존재하지 않는다
     * WHEN 존재하지 않는 출발역, 도착역을 입력한다
     * THEN 예외가 발생한다
     */
    @Test
    @DisplayName("존재하지 않는 출발역, 도착역을 입력하면 예외를 발생시킨다.")
    void exceptionTest3() {
        // when
        ExtractableResponse<Response> pathResponse = PathTestHelper.findPath(-1L, -2L);

        // then
        assertThat(pathResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}