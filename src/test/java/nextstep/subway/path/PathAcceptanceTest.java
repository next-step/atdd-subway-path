package nextstep.subway.path;

import nextstep.subway.line.acceptance.LineSteps;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationSteps;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;

public class PathAcceptanceTest {

    @BeforeEach
    void setUp(){
        StationSteps.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("역삼역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("선릉역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("매봉역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("도곡역").as(StationResponse.class);
        StationSteps.지하철역_등록되어_있음("한티역").as(StationResponse.class);

        //LineSteps.지하철_노선_등록되어_있음(new LineRequest("2호선", "green",).as(LineResponse.class);
    }
}
