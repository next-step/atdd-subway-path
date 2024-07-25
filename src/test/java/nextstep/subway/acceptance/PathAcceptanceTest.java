package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.TestFixture.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineRequest;

@DisplayName("지하철 경로 검색")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PathAcceptanceTest {
    private Long 강남역;
    private Long 역삼역;
    private Long 교대역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 신논현역;
    private Long 잠실역;
    private Long 이호선;
    private Long 삼호선;
    private Long 신분당선;

    @BeforeEach
    public void setUp() {
        신논현역 = createStationAndGetId("신논현역");

        교대역 = createStationAndGetId("교대역");
        강남역 = createStationAndGetId("강남역");
        역삼역 = createStationAndGetId("역삼역");

        남부터미널역 = createStationAndGetId("남부터미널역");
        양재역 = createStationAndGetId("양재역");

        잠실역 = createStationAndGetId("논현역");

        신분당선 = createLineAndGetId(new LineRequest("신분당선", "red", 신논현역, 강남역, 11));
        이호선 = createLineAndGetId(new LineRequest("2호선", "green", 교대역, 강남역, 7));
        삼호선 = createLineAndGetId(new LineRequest("3호선", "orange", 교대역, 남부터미널역, 3));

        addSection(이호선, 강남역, 역삼역, 13);
        addSection(삼호선, 남부터미널역, 양재역, 5);
        addSection(신분당선, 강남역, 양재역, 10);
    }

    /**
     * Given 같은 노선에 존재하는 두 역을 생성하고
     * When 지하철 경로를 조회하면
     * Then 경로가 정상적으로 조회된다
     */
    @Test
    @DisplayName("같은 노선에 존재하는 두 역을 조회하는 경우 경로가 정상적으로 조회된다")
    void findPathInSameLine() {
        // when
        ExtractableResponse<Response> response = getPaths(교대역, 역삼역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(교대역, 강남역, 역삼역);
        assertThat(getResponseDistance(response)).isEqualTo(20);
    }

    /**
     * Given 출발역에서 한번 환승이 필요한 경로를 생성하고
     * When 지하철 경로를 조회하면
     * Then 경로가 정상적으로 조회된다
     */
    @Test
    @DisplayName("출발역에서 한번 환승을 해야 도착역에 다다를 수 있는 경우 경로가 정상적으로 조회된다")
    void findPathWithOneTransfer() {
        // when
        ExtractableResponse<Response> response = getPaths(교대역, 신논현역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(교대역, 강남역, 신논현역);
        assertThat(getResponseDistance(response)).isEqualTo(18);
    }

    /**
     * Given 출발역에서 두번 환승이 필요한 경로를 생성하고
     * When 지하철 경로를 조회하면
     * Then 경로가 정상적으로 조회된다
     */
    @Test
    @DisplayName("출발역에서 두번 환승을 해야 도착역에 다다를 수 있는 경우 경로가 정상적으로 조회된다")
    void findPathWithTwoTransfers() {
        // given
        Long source = 신논현역;
        Long target = 남부터미널역;

        // when
        ExtractableResponse<Response> response = getPaths(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(신논현역, 강남역, 교대역, 남부터미널역);
        assertThat(getResponseDistance(response)).isEqualTo(21);
    }

    /**
     * Given 출발역과 도착역이 같은 경우를 생성하고
     * When 지하철 경로를 조회하면
     * Then 역이 하나만 조회된다
     */
    @Test
    @DisplayName("출발역과 도착역이 같은 경우 역이 하나만 조회된다.")
    void findPathWhenSourceAndTargetAreSame() {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponseStationIds(response)).containsExactly(강남역);
        assertThat(getResponseDistance(response)).isEqualTo(0);
    }

    /**
     * Given 출발역과 도착역이 연결되지 않은 경우를 생성하고
     * When 지하철 경로를 조회하면
     * Then 예외가 발생한다
     */
    @Test
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외가 발생한다")
    void findPathWhenSourceAndTargetAreNotConnected() {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, 잠실역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 조회하려는 출발역에 null을 입력하고
     * When 지하철 경로를 조회하면
     * Then 예외가 발생한다
     */
    @Test
    @DisplayName("조회하려는 출발역이 null인 경우 예외가 발생한다")
    void findPathWhenSourceStationIsNull() {
        // given & when
        ExtractableResponse<Response> response = getPaths(null, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 존재하지 않는 출발역으로
     * When 지하철 경로를 조회하면
     * Then 예외가 발생한다
     */
    @Test
    @DisplayName("존재하지 않은 출발역을 조회하는 경우 예외가 발생한다")
    void findPathWhenSourceStationNotFound() {
        // given & when
        ExtractableResponse<Response> response = getPaths(999L, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 조회하려는 도착역에 null을 입력하고
     * When 지하철 경로를 조회하면
     * Then 예외가 발생한다
     */
    @Test
    @DisplayName("조회하려는 출발역이 null인 경우 예외가 발생한다")
    void findPathWhenTargetStationIsNull() {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, null);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 존재하지 않는 도착역으로
     * When 지하철 경로를 조회하면
     * Then 예외가 발생한다
     */
    @Test
    @DisplayName("존재하지 않는 도착역을 조회하는 경우 예외가 발생한다")
    void findPathWhenTargetStationNotFound() {
        // given & when
        ExtractableResponse<Response> response = getPaths(강남역, 999L);

        // then
        int statusCode = response.statusCode();
        assertThat(statusCode).isBetween(400, 599);
    }


    private List<Long> getResponseStationIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    private int getResponseDistance(ExtractableResponse<Response> response) {
        return response.jsonPath().getInt("distance");
    }
}
