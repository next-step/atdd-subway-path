package nextstep.subway.acceptance.path;

import static nextstep.subway.acceptance.line.LineUtils.*;
import static nextstep.subway.acceptance.line.SectionUtils.*;
import static nextstep.subway.acceptance.path.PathUtils.지하철역_경로조회;
import static nextstep.subway.acceptance.station.StationUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역_id;
    private Long 강남역_id;
    private Long 양재역_id;
    private Long 남부터미널역_id;
    private Long 이호선_id;
    private Long 신분당선_id;
    private Long 삼호선_id;

    /**
     * 교대역    --- *2호선* ---   강남역 |                        | *3호선*                   *신분당선* |
     * | 남부터미널역  --- *3호선* ---   양재
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역_id = 지하철역_생성_후_id_추출(교대역);
        강남역_id = 지하철역_생성_후_id_추출(강남역);
        양재역_id = 지하철역_생성_후_id_추출(양재역);
        남부터미널역_id = 지하철역_생성_후_id_추출(남부터미널역);

        이호선_id = 지하철노선_생성_후_ID_반환(이호선, "green", 교대역_id, 강남역_id, 10L);
        신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 강남역_id, 양재역_id, 10L);
        삼호선_id = 지하철노선_생성_후_ID_반환(삼호선, "orange", 교대역_id, 남부터미널역_id, 2L);

        지하철구간_생성(삼호선_id, 남부터미널역_id, 양재역_id, 3L);
    }

    /**
     * Given 지하철 노선이 주어지고 When 출발역과 도착역을 조회하면 Then 최단 경로가 조회된다.
     */
    @DisplayName("경로가 조회된다.")
    @Test
    void pathTest() {
        //when
        ExtractableResponse<Response> 지하철경로_조회_응답 = 지하철역_경로조회(교대역_id, 양재역_id);

        //then
        List<String> 지하철경로의_역이름_목록 = responseToStationNames(지하철경로_조회_응답);
        assertThat(지하철경로의_역이름_목록).containsExactlyInAnyOrder(교대역, 남부터미널역, 양재역);
    }

    /**
     * Given 지하철 노선이 주어지고 When 출발역과 도착역이 같은 경우 Then 예외가 발생한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 예외가 발생한다.")
    @Test
    void pathExceptionTest() {
        //when
        ExtractableResponse<Response> 지하철경로_조회_응답 = 지하철역_경로조회(교대역_id, 교대역_id);

        //then
        assertThat(지하철경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 주어지고 When 출발역과 도착역이 연결되어 있지 않은 경우 Then 예외가 발생한다.
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 예외가 발생한다.")
    @Test
    void pathExceptionTest2() {
        //given
        Long 역삼역_id = 지하철역_생성_후_id_추출(역삼역);

        //when
        ExtractableResponse<Response> 지하철경로_조회_응답 = 지하철역_경로조회(강남역_id, 역삼역_id);

        //then
        assertThat(지하철경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 주어지고 When 존재하지 않은 출발역을 조회하는 경우 Then 예외가 발생한다.
     */
    @DisplayName("존재하지 않은 출발역을 조회하는 경우 예외가 발생한다.")
    @Test
    void pathExceptionTest3() {
        //when
        ExtractableResponse<Response> 지하철경로_조회_응답 = 지하철역_경로조회(100L, 남부터미널역_id);

        //then
        assertThat(지하철경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 주어지고 When 존재하지 않은 도착역을 조회하는 경우 Then 예외가 발생한다.
     */
    @DisplayName("존재하지 않은 도착역을 조회하는 경우 예외가 발생한다.")
    @Test
    void pathExceptionTest4() {
        //when
        ExtractableResponse<Response> 지하철경로_조회_응답 = 지하철역_경로조회(강남역_id, 100L);

        //then
        assertThat(지하철경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


}
