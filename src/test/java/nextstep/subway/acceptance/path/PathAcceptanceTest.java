package nextstep.subway.acceptance.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.fixture.LineFixture;
import nextstep.subway.acceptance.fixture.SectionFixture;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.PathSourceTargetNotConnectedException;
import nextstep.subway.exception.PathSourceTargetSameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static nextstep.subway.acceptance.util.RestAssuredUtil.생성_요청;
import static nextstep.subway.acceptance.util.RestAssuredUtil.조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@AcceptanceTest
@DisplayName("지하철 경로 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {
    private ExtractableResponse<Response> 교대역;
    private ExtractableResponse<Response> 강남역;
    private ExtractableResponse<Response> 양재역;
    private ExtractableResponse<Response> 남부터미널역;
    private ExtractableResponse<Response> 이호선;
    private ExtractableResponse<Response> 신분당선;
    private ExtractableResponse<Response> 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void before() {
        교대역 = 생성_요청(
                StationFixture.createStationParams("교대역"),
                "/stations");
        강남역 = 생성_요청(
                StationFixture.createStationParams("강남역"),
                "/stations");
        양재역 = 생성_요청(
                StationFixture.createStationParams("양재역"),
                "/stations");
        남부터미널역 = 생성_요청(
                StationFixture.createStationParams("남부터미널역"),
                "/stations");
        이호선 = 생성_요청(
                LineFixture.createLineParams("2호선", "GREEN", 교대역.jsonPath().getLong("id"), 강남역.jsonPath().getLong("id"), 10L),
                "/lines");
        신분당선 = 생성_요청(
                LineFixture.createLineParams("신분당선", "RED", 강남역.jsonPath().getLong("id"), 양재역.jsonPath().getLong("id"), 10L),
                "/lines");
        삼호선 = 생성_요청(
                LineFixture.createLineParams("3호선", "ORANGE", 교대역.jsonPath().getLong("id"), 남부터미널역.jsonPath().getLong("id"), 2L),
                "/lines");
        생성_요청(
                SectionFixture.createSectionParams(남부터미널역.jsonPath().getLong("id"), 양재역.jsonPath().getLong("id"), 3L),
                "/lines/" + 삼호선.jsonPath().getLong("id") + "/sections"
        );
    }


    /**
     * when 출발역과 마지막역을 기준으로 조회한다.
     * then 출발역과 마지막역 사이의 모든 역을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 경로를 조회한다.")
    void 지하철_경로_조회한다() {
        //when
        ExtractableResponse<Response> 고속터미널역_신사역_경로_조회 = 조회_요청("/paths?source=" + 교대역.jsonPath().getLong("id")
                + "&target=" + 양재역.jsonPath().getLong("id"));

        //then
        assertThat(고속터미널역_신사역_경로_조회.jsonPath().getList("stations")).containsExactly(교대역.jsonPath().get(), 남부터미널역.jsonPath().get(), 양재역.jsonPath().get());
        assertThat(고속터미널역_신사역_경로_조회.jsonPath().getDouble("distance")).isEqualTo(5);
    }

    /**
     * when 출발역과 마지막역을 동일한역으로 조회하면
     * then 에러가 발생한다.
     */
    @Test
    @DisplayName("출발역과 마지막역을 동일한 역으로 조회하면 에러가 발생한다.")
    void 출발역과_도착역이_같은_경로에러() {
        //when
        //then
        assertThatThrownBy(() -> 조회_요청("/paths?source=" + 교대역.jsonPath().getLong("id")
                + "&target=" + 교대역.jsonPath().getLong("id"))).isInstanceOf(PathSourceTargetSameException.class);
    }

    /**
     * given 역을 등록한다.
     * when 출발역과 마지막역을 구간에 연결되어있지 않은역으로 조회하면
     * then 에러가 발생한다.
     */
    @Test
    @DisplayName("출발역과 마지막역을 구간에 연결되어있지 역으로 조회하면 에러가 발생한다.")
    void 출발역과_도착역이_구간에_존재하지않으면_에러() {
        //given
        ExtractableResponse<Response> 삼성역 = 생성_요청(
                StationFixture.createStationParams("삼성역"),
                "/stations");
        ExtractableResponse<Response> 역삼역 = 생성_요청(
                StationFixture.createStationParams("역삼역"),
                "/stations");

        //when
        //then
        assertThatThrownBy(() -> 조회_요청("/paths?source=" + 삼성역.jsonPath().getLong("id")
                + "&target=" + 역삼역.jsonPath().getLong("id"))).isInstanceOf(PathSourceTargetNotConnectedException.class);
    }

    /**
     * given 역을 등록한다.
     * when 출발역과 마지막역을 존재하지 않은역으로 조회하면
     * then 에러가 발생한다.
     */
    @Test
    @DisplayName("출발역과 마지막역을 존재하지않는 역으로 조회하면 에러가 발생한다.")
    void 출발역과_도착역이_존재하지않으면_에러() {
        //given

        //when
        //then
        assertThatThrownBy(() -> 조회_요청("/paths?source=" + 888
                + "&target=" + 999)).isInstanceOf(NotFoundException.class);
    }
}
