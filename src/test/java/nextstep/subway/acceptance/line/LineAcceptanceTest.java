package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.station.StationApiRequester;
import subway.line.dto.LineCreateRequest;
import subway.line.dto.LineUpdateRequest;
import nextstep.subway.utils.JsonPathUtil;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철노선 관련 기능")
@AcceptanceTest
public class LineAcceptanceTest {
    Long 잠실역id;
    Long 용산역id;
    Long 건대입구역id;
    Long 성수역id;

    @BeforeEach
    void setUp() {
        잠실역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("잠실역"));
        용산역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("용산역"));
        건대입구역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("건대입구역"));
        성수역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("성수역"));
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void createLine() {
        //when
        LineCreateRequest 이호선 = new LineCreateRequest(
                "2호선",
                "green",
                잠실역id,
                용산역id,
                10
        );
        ExtractableResponse<Response> response = LineApiRequester.createLineApiCall(이호선);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(JsonPathUtil.getIds(LineApiRequester.findLinesApiCall()))
                .containsExactly(JsonPathUtil.getId(response));
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void showLiens() {
        //given
        LineCreateRequest 이호선 = new LineCreateRequest(
                "2호선",
                "green",
                잠실역id,
                용산역id,
                10
        );
        Long 이호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(이호선));

        LineCreateRequest 일호선 = new LineCreateRequest(
                "1호선",
                "blue",
                건대입구역id,
                성수역id,
                10
        );
        Long 일호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(일호선));

        //when
        ExtractableResponse<Response> response = LineApiRequester.findLinesApiCall();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(JsonPathUtil.getIds(response)).containsExactly(이호선id, 일호선id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다")
    @Test
    void showLine() {
        //given
        LineCreateRequest 이호선 = new LineCreateRequest(
                "2호선",
                "green",
                잠실역id,
                용산역id,
                10
        );
        Long 이호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(이호선));

        //when
        ExtractableResponse<Response> response = LineApiRequester.findLineApiCall(이호선id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(JsonPathUtil.getId(response)).isEqualTo(이호선id);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void updateLine() {
        //given
        LineCreateRequest 이호선 = new LineCreateRequest(
                "2호선",
                "green",
                잠실역id,
                용산역id,
                10
        );
        Long 이호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(이호선));

        //when
        LineUpdateRequest 일호선 = new LineUpdateRequest("1호선", "blue");
        ExtractableResponse<Response> response = LineApiRequester.updateLineApiCall(이호선id, 일호선);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> findLineResponse = LineApiRequester.findLineApiCall(이호선id);
        assertThat(JsonPathUtil.getString(findLineResponse, "name")).isEqualTo("1호선");
        assertThat(JsonPathUtil.getString(findLineResponse, "color")).isEqualTo("blue");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        //given
        LineCreateRequest 이호선 = new LineCreateRequest(
                "2호선",
                "green",
                잠실역id,
                용산역id,
                10
        );
        Long 이호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(이호선));

        //when
        ExtractableResponse<Response> response = LineApiRequester.deleteLineApiCall(이호선id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(LineApiRequester.findLineApiCall(이호선id).asPrettyString()).isEqualTo("존재하지 않는 노선입니다.");
    }
}
