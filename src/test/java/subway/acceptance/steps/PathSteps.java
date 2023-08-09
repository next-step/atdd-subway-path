package subway.acceptance.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.factory.SubwayNameFactory.고속터미널역;
import static subway.factory.SubwayNameFactory.구호선;
import static subway.factory.SubwayNameFactory.논현역;
import static subway.factory.SubwayNameFactory.반포역;
import static subway.factory.SubwayNameFactory.사평역;
import static subway.factory.SubwayNameFactory.신논현역;
import static subway.factory.SubwayNameFactory.신분당선;
import static subway.factory.SubwayNameFactory.칠호선;

import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.stream.Collectors;
import subway.acceptance.utils.RestAssuredClient;
import subway.dto.LineRequest;
import subway.dto.PathResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.dto.ErrorResponse;
import subway.exception.error.SubwayErrorCode;

public class PathSteps {

    /**
     *
     * 논현역   --- *신분당선* ---  신논현역
     * |                        |
     * 반포역                     |
     * |                        |
     * *7호선*                   *9호선*
     * |                        |
     * 고속터미널역 --- *9호선* --- 사평역
     *
     */


    private static final String STATION_PATH = "/stations";
    private static final String LINE_PATH = "/lines";

    public static void 지하철역_등록() {
        RestAssuredClient.requestPost(STATION_PATH, StationRequest.builder().name(논현역).build());
        RestAssuredClient.requestPost(STATION_PATH, StationRequest.builder().name(신논현역).build());
        RestAssuredClient.requestPost(STATION_PATH, StationRequest.builder().name(반포역).build());
        RestAssuredClient.requestPost(STATION_PATH, StationRequest.builder().name(사평역).build());
        RestAssuredClient.requestPost(STATION_PATH, StationRequest.builder().name(고속터미널역).build());
    }

    public static void 노선_등록() {
        LineRequest 신분당선_노선_등록_파라미터 = LineRequest.builder()
            .name(신분당선)
            .color("bg-red-600")
            .upStationId(1L)
            .downStationId(2L)
            .distance(2L)
            .build();
        RestAssuredClient.requestPost(LINE_PATH, 신분당선_노선_등록_파라미터);

        LineRequest 칠호선_노선_등록_파라미터 = LineRequest.builder()
            .name(칠호선)
            .color("bg-green-600")
            .upStationId(1L)
            .downStationId(3L)
            .distance(1L)
            .build();
        RestAssuredClient.requestPost(LINE_PATH, 칠호선_노선_등록_파라미터);


        LineRequest 구호선_노선_등록_파라미터 = LineRequest.builder()
            .name(구호선)
            .color("bg-brown-600")
            .upStationId(4L)
            .downStationId(2L)
            .distance(5L)
            .build();
        RestAssuredClient.requestPost(LINE_PATH, 구호선_노선_등록_파라미터);
    }

    public static void 구간_등록() {
        SectionRequest 칠호선_고속터미널_반포_구간_등록_파라미터 = SectionRequest.builder()
            .upStationId(5L)
            .downStationId(3L)
            .distance(4L)
            .build();
        RestAssuredClient.requestPost(LINE_PATH + "/" + 2 + "/sections",
            칠호선_고속터미널_반포_구간_등록_파라미터);

        SectionRequest 구호선_고속터미널_사평_구간_등록_파라미터 = SectionRequest.builder()
            .upStationId(5L)
            .downStationId(4L)
            .distance(2L)
            .build();
        RestAssuredClient.requestPost(LINE_PATH + "/" + 3 + "/sections",
            구호선_고속터미널_사평_구간_등록_파라미터);
    }

    public static ValidatableResponse 경로_조회(Long source, Long target) {
        return RestAssuredClient.requestGet("/paths?source=" + source + "&target=" + target);
    }

    public static void 조회한_경로_검증(PathResponse response, Long distance, List<String> stationNames) {
        assertThat(response.getDistance()).isEqualTo(distance);
        assertThat(response.getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
            .containsAll(stationNames);
    }

    public static void 경로_조회_예외_검증(ErrorResponse response, String message) {
        assertThat(response.getMessage()).isEqualTo(message);
    }

}
