package nextstep.subway.path.acceptance;

import static nextstep.subway.line.acceptance.LineSteps.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import static nextstep.subway.station.StationSteps.*;
import static nextstep.subway.path.acceptance.PathSteps.*;


import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("최단 경로 탐색 인수테스트")
public class PathFinderAcceptanceTest extends AcceptanceTest {
    StationResponse 강남역;
    StationResponse 역삼역;
    StationResponse 선릉역;
    StationResponse 교대역;
    StationResponse 남부터미널역;
    StationResponse 양재역;
    StationResponse 매봉역;
    StationResponse 도곡역;
    StationResponse 한티역;

    LineResponse 이호선;

    @BeforeEach
    void 역_노선_세팅(){
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        역삼역 = 지하철역_등록되어_있음("역삼역").as(StationResponse.class);
        선릉역 = 지하철역_등록되어_있음("선릉역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        매봉역 = 지하철역_등록되어_있음("매봉역").as(StationResponse.class);
        도곡역 = 지하철역_등록되어_있음("도곡역").as(StationResponse.class);
        한티역 = 지하철역_등록되어_있음("한티역").as(StationResponse.class);

        LineResponse 이호선 = 지하철_노선_등록되어_있음(new LineRequest("2호선", "green", 강남역.getId(), 역삼역.getId(), 5)).as(LineResponse.class);
        지하철_노선에_지하철역_등록_요청(이호선, 역삼역, 선릉역, 5);
        지하철_노선에_지하철역_등록_요청(이호선, 교대역, 강남역, 5);

        LineResponse 삼호선 = 지하철_노선_등록되어_있음(new LineRequest("3호선", "orange", 교대역.getId(), 남부터미널역.getId(), 5)).as(LineResponse.class);
        지하철_노선에_지하철역_등록_요청(삼호선, 남부터미널역, 양재역, 5);
        지하철_노선에_지하철역_등록_요청(삼호선, 양재역, 매봉역, 5);
        지하철_노선에_지하철역_등록_요청(삼호선, 매봉역, 도곡역, 5);

        LineResponse 신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "red", 강남역.getId(), 양재역.getId(), 5)).as(LineResponse.class);

        LineResponse 분당선 = 지하철_노선_등록되어_있음(new LineRequest("분당선", "yellow", 선릉역.getId(), 한티역.getId(), 5)).as(LineResponse.class);
        지하철_노선에_지하철역_등록_요청(분당선, 한티역, 도곡역, 5);
    }

    @DisplayName("최단 경로 조회")
    @Test
    void 최단_경로_조회(){
        //given
        long source = 강남역.getId();
        long target = 한티역.getId();
        //when
        ExtractableResponse<Response> response = PathSteps.최단경로_요청(source, target);
        //then
        최단거리_확인(response, 15);
        최단경로_확인(response, Arrays.asList("강남역", "역삼역", "선릉역", "한티역"));
    }

    @DisplayName("시작과 끝이 같을 때")
    @Test
    void 시작과_끝_같을_때(){
        //given
        long source = 강남역.getId();
        long target = 강남역.getId();
        //when
        ExtractableResponse<Response> response = PathSteps.최단경로_요청(source, target);
        //then
        응답_400_코드(response);
    }

    @DisplayName("두 역이 만날 수 없을 때")
    @Test
    void 두_역이_만날_수_없을_때(){
        //given
        StationResponse 동춘역 = 지하철역_등록되어_있음("동춘역").as(StationResponse.class);
        StationResponse 동막역 = 지하철역_등록되어_있음("동막역").as(StationResponse.class);
        LineResponse 인천1호선 = 지하철_노선_등록되어_있음(new LineRequest("인천1호선", "blue", 동춘역.getId(), 동막역.getId(), 5)).as(LineResponse.class);

        long source = 강남역.getId();
        long target = 동막역.getId();
        //when
        ExtractableResponse<Response> response = PathSteps.최단경로_요청(source, target);
        //then
        응답_400_코드(response);
    }

}
