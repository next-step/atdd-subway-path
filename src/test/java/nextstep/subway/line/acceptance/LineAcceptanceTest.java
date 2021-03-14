package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineResponse redLine;
    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 정자역;


    private LineRequest lineRequest;

    @BeforeEach
    void setup() {
        super.setUp();

        int distance = 6;
        강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
        판교역 = 지하철역_생성_요청("판교역").as(StationResponse.class);
        정자역 = 지하철역_생성_요청("정자역").as(StationResponse.class);

        lineRequest = new LineRequest("신분당선", "red", 판교역.getId(), 정자역.getId(), distance);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성요청(lineRequest);
        // then
        지하철_노선_응답_확인(response.statusCode(), HttpStatus.CREATED);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        StationResponse 서초역 = 지하철역_생성_요청("서초역").as(StationResponse.class);
        StationResponse 역삼역 = 지하철역_생성_요청("역삼역").as(StationResponse.class);
        LineRequest greenLineRequest = setStationData("2호선", "green", 서초역.getId(), 역삼역.getId(), 3);

        StationResponse 석촌역 = 지하철역_생성_요청("석촌역").as(StationResponse.class);
        StationResponse 천호역 = 지하철역_생성_요청("천호역").as(StationResponse.class);
        LineRequest pinkLineRequest = setStationData("8호선", "pink", 석촌역.getId(), 천호역.getId(), 10);

        // given
        LineResponse lineResponse1 = 지하철_노선_생성요청(greenLineRequest).as(LineResponse.class);
        LineResponse lineResponse2 = 지하철_노선_생성요청(pinkLineRequest).as(LineResponse.class);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_응답_확인(response.statusCode(), HttpStatus.OK);

        // 지하철_노선_목록_포함됨
        List<LineResponse> lineRequestResponses = Arrays.asList(lineResponse1, lineResponse2);
        지하철_노선_목록_응답_확인(response, lineRequestResponses);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        StationResponse 신도림역 = 지하철역_생성_요청("신도림역").as(StationResponse.class);
        StationResponse 영등포역 = 지하철역_생성_요청("영등포역").as(StationResponse.class);

        LineRequest blueLineRequest = setStationData("1호선", "blue", 신도림역.getId(), 영등포역.getId(), 10);
        // given
        LineResponse line = 지하철_노선_생성요청(blueLineRequest).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line.getId());

        // then
        지하철_노선_요청에대한_응답_확인(response, line);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        StationResponse 상수역 = 지하철역_생성_요청("상수역").as(StationResponse.class);
        StationResponse 이태원역 = 지하철역_생성_요청("이태원역").as(StationResponse.class);

        LineRequest brownLineRequest = setStationData("6호선", "brown", 상수역.getId(), 이태원역.getId(), 10);

        // given
        LineResponse line = 지하철_노선_생성요청(brownLineRequest).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(line);

        // then
        지하철_노선_요청에대한_응답_확인(response, line);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {

        // given
        LineResponse lineResponse = 지하철_노선_생성요청(lineRequest).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineResponse);

        // then
        지하철_노선_응답_확인(response.statusCode(), HttpStatus.NO_CONTENT);
    }

    @DisplayName("이미 등록된 이름으로 등록 요청 시 에러 응답")
    @Test
    void subwayExistNameException() {
        //given
        지하철_노선_생성요청(lineRequest);
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성요청(lineRequest);
        // then
        지하철_노선_응답_확인(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    LineRequest setStationData(String line, String color, long upStationId, long downStationId, int distance) {
        return new LineRequest(line, color, upStationId, downStationId, distance);
    }
}
