package nextstep.subway.path;

import static nextstep.subway.line.acceptance.LineSteps.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import static nextstep.subway.station.StationSteps.*;
import static nextstep.subway.path.PathSteps.*;


import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PathAcceptanceTest extends AcceptanceTest {
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

    @Test
    void 최단_경로_조회(){
        //given
        int source = 1;
        int target = 9;
        //when
        ExtractableResponse<Response> response = PathSteps.최단경로_요청(source, target);
        //then
        최단거리_확인(response, 15);
        최단경로_확인(response, Arrays.asList("강남역", "역삼역", "선릉역", "한티역"));
    }

}
