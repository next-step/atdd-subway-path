package nextstep.subway;

import nextstep.subway.controller.dto.*;
import nextstep.subway.exception.ExceptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static nextstep.subway.fixture.LineFixture.SHINBUNDANG_LINE;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;

@DisplayName("지하철 경로 조회 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 강남역_ID;
    private Long 선릉역_ID;
    private Long 양재역_ID;
    private Long 역삼역_ID;
    private Long 신대방역_ID;
    private Long 신림역_ID;
    private Long 봉천역_ID;

    private Long 신분당선_ID;
    private Long 분당선_ID;
    private Long 일호선_ID;
    private Long 이호선_ID;
    private Long 삼호선_ID;

    /**
     * GIVEN 지하철 역을 생성하고
     * GIVEN 생성한 지하철역이 모두 연결된 노선을 생성한다
     *
     * 역삼역    --- *1호선*(10) ---   양재역
     * |                        |
     * *2호선*(10)                   *분당선*(10)
     * |                        |
     * 강남역    --- *신분당호선*(10) ---    선릉역
     * <p>
     * 강남역    --- *3호선*(10) ---    선릉역
     */
    @BeforeEach
    void setFixture() {
        강남역_ID = 지하철역_생성_요청(GANGNAM_STATION.toCreateRequest());
        선릉역_ID = 지하철역_생성_요청(SEOLLEUNG_STATION.toCreateRequest());
        양재역_ID = 지하철역_생성_요청(YANGJAE_STATION.toCreateRequest());
        역삼역_ID = 지하철역_생성_요청(YEOKSAM_STATION.toCreateRequest());
        신대방역_ID = 지하철역_생성_요청(YEOKSAM_STATION.toCreateRequest());
        신림역_ID = 지하철역_생성_요청(YEOKSAM_STATION.toCreateRequest());
        봉천역_ID = 지하철역_생성_요청(YEOKSAM_STATION.toCreateRequest());

        신분당선_ID = 노선_생성_요청(SHINBUNDANG_LINE.toCreateRequest(강남역_ID, 선릉역_ID));
        분당선_ID = 노선_생성_요청(SHINBUNDANG_LINE.toCreateRequest(선릉역_ID, 양재역_ID));
        일호선_ID = 노선_생성_요청(SHINBUNDANG_LINE.toCreateRequest(양재역_ID, 역삼역_ID));
        이호선_ID = 노선_생성_요청(SHINBUNDANG_LINE.toCreateRequest(역삼역_ID, 강남역_ID));
        삼호선_ID = 노선_생성_요청(SHINBUNDANG_LINE.toCreateRequest(신대방역_ID, 신림역_ID));
    }

    private Long 지하철역_생성_요청(StationCreateRequest stationCreateRequest) {
        return 지하철역_생성_요청(stationCreateRequest, CREATED.value())
                .as(StationResponse.class).getId();
    }

    private Long 노선_생성_요청(LineCreateRequest lineCreateRequest) {
        return 노선_생성_요청(lineCreateRequest, CREATED.value())
                .as(LineResponse.class).getId();
    }

    /**
     * 역삼역    --- *1호선*(10) ---   양재역
     * |                        |
     * *2호선*(10)                   *분당선*(10)
     * |                        |
     * 강남역    --- *신분당호선*(10) ---    선릉역
     * <p>
     * 강남역    --- *3호선*(10) ---    선릉역
     * <p>
     * WHEN 경로 조회시 출발역과 도착역이 같은 경우
     * Then 경로 조회를 할 수 없다
     */
    @Test
    void 실패_경로_조회시_출발역과_도착역이_같은_경우_경로를_조회할_수_없다() {
        // given
        Map<String, String> params = Map.of("source", 강남역_ID.toString(), "target", 강남역_ID.toString());

        // when
        String message = get("/paths", OK.value(), params)
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("출발역과 도착역이 같은 경우 경로를 조회할 수 없습니다.");
    }

    /**
     * 역삼역    --- *1호선*(10) ---   양재역
     * |                        |
     * *2호선*(10)                   *분당선*(10)
     * |                        |
     * 강남역    --- *신분당호선*(10) ---    선릉역
     * <p>
     * 강남역    --- *3호선*(10) ---    선릉역
     * <p>
     * WHEN 경로 조회시 출발역과 도착역이 연결되어 있지 않은 경우
     * Then 경로 조회를 할 수 없다
     */
    @Test
    void 실패_경로_조회시_출발역과_도착역이_연결되어_있지_않은_경우_경로를_조회할_수_없다() {
        // given
        Map<String, String> params = Map.of("source",  강남역_ID.toString(), "target", 신대방역_ID.toString());

        // when
        String message = get("/paths", OK.value(), params)
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    /**
     * 역삼역    --- *1호선*(10) ---   양재역
     * |                        |
     * *2호선*(10)                   *분당선*(10)
     * |                        |
     * 강남역    --- *신분당호선*(10) ---    선릉역
     * <p>
     * 강남역    --- *3호선*(10) ---    선릉역
     * <p>
     * WHEN 경로 조회시 존재하지 않는 출발역일 경우
     * Then 경로 조회를 할 수 없다
     */
    @Test
    void 실패_경로_조회시_노선에_존재하지_않는_출발역일_경우_경로를_조회할_수_없다() {
        // given
        Map<String, String> params = Map.of("source", 강남역_ID.toString(), "target", 봉천역_ID.toString());

        // when
        String message = get("/paths", OK.value(), params)
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("노선에 존재하지 않는 지하철역입니다.");
    }

    /**
     * 역삼역    --- *1호선*(10) ---   양재역
     * |                        |
     * *2호선*(10)                   *분당선*(10)
     * |                        |
     * 강남역    --- *신분당호선*(10) ---    선릉역
     * <p>
     * 강남역    --- *3호선*(10) ---    선릉역
     * <p>
     * WHEN 경로 조회시 존재하지 않는 도착역일 경우
     * Then 경로 조회를 할 수 없다
     */
    @Test
    void 실패_경로_조회시_노선에_존재하지_않는_도착역일_경우_경로를_조회할_수_없다() {
        // given
        Map<String, String> params = Map.of("source", 봉천역_ID.toString(),"target", 강남역_ID.toString());

        // when
        String message = get("/paths", OK.value(), params)
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("노선에 존재하지 않는 지하철역입니다.");
    }

    /**
     * 역삼역    --- *1호선*(10) ---   양재역
     * |                        |
     * *2호선*(10)                   *분당선*(10)
     * |                        |
     * 강남역    --- *신분당호선*(10) ---    선릉역
     * <p>
     * 강남역    --- *3호선*(10) ---    선릉역
     * <p>
     * WHEN 경로 조회시 출발역과 도착역이 연결되어 있는 경우
     * Then 경로 조회를 할 수 없다
     */
    @Test
    void 성공_경로_조회시_출발역과_도착역이_연결되어_있을_경우_경로를_조회할_수_없다() {
        // given
        Map<String, String> params = Map.of("source", 강남역_ID.toString(), "target", 역삼역_ID.toString());

        // when
        PathResponse pathResponse = get("/paths", OK.value(), params)
                .as(PathResponse.class);

        // then
        assertAll(
                () -> assertThat(pathResponse.getDistance()).isEqualTo(10L),
                () -> assertThat(pathResponse.getStations()).hasSize(2)
                        .extracting("id", "name")
                        .containsExactly(
                                tuple(1L, "강남역"),
                                tuple(4L, "역삼역")
                        )
        );
    }

}
