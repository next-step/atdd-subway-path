package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.acceptance.LineSteps.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final String LINE_NAME_1 = "1호선";
    private final String LINE_NAME_2 = "2호선";
    private final String COLOR_1 = "blue";
    private final String COLOR_2 = "green";
    private long STATION_1;
    private long STATION_2;
    private final int DISTANCE = 10;

    private LineRequest firstRequest;
    private LineRequest secondRequest;

    @BeforeEach
    void before() {
        STATION_1 = 지하철역_생성_요청("강남역");
        STATION_2 = 지하철역_생성_요청("신촌역");
        firstRequest = new LineRequest(LINE_NAME_1, COLOR_1, STATION_1, STATION_2, DISTANCE);
        secondRequest = new LineRequest(LINE_NAME_2, COLOR_2, STATION_1, STATION_2, DISTANCE);

    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        // when
        ExtractableResponse<Response> response = 노선_생성_요청(firstRequest);

        // then
        // 지하철_노선_생성됨
        노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        노선_등록되어_있음(secondRequest);

        // when
        ExtractableResponse<Response> response = 노선_생성_요청(secondRequest);

        // then
        노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> firstMockLine = 노선_등록되어_있음(firstRequest);
        ExtractableResponse<Response> secondMockLine = 노선_등록되어_있음(secondRequest);

        /// when
        ExtractableResponse<Response> response = 노선_목록_조회_요청();

        // then
        노선_목록_응답됨(response);
        노선_목록_포함됨(response, Arrays.asList(firstMockLine, secondMockLine));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> mockLine = 노선_등록되어_있음(firstRequest);

        // when
        ExtractableResponse<Response> response = 노선_조회_요청(mockLine);

        // then
        노선_응답됨(response, mockLine);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> mockLine = 노선_등록되어_있음(firstRequest);

        // when
        ExtractableResponse<Response> response = 노선_수정_요청(mockLine, secondRequest);

        // then
        노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> mockLine = 노선_등록되어_있음(firstRequest);

        // when
        ExtractableResponse<Response> response = 노선_제거_요청(mockLine);

        // then
        노선_삭제됨(response);
    }

}
