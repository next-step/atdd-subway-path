package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends CommonAcceptanceTest {
    private Long 교대역Id;
    private Long 남부터미널역Id;
    private Long 양재역Id;
    private Long 강남역Id;
    private Long 역삼역Id;

    private static int 교대역_남부터미널역_거리 = 3;
    private static int 남부터미널역_양재역_거리 = 4;
    private static int 교대역_강남역_거리 = 1;
    private static int 양재역_강남역_거리 = 2;
    private static int 강남역_역삼역_거리 = 5;

    @BeforeEach
    void setStation() {
        교대역Id = extractResponseId(StationRestAssuredCRUD.createStation("교대역"));
        남부터미널역Id = extractResponseId(StationRestAssuredCRUD.createStation("남부터미널역"));
        양재역Id = extractResponseId(StationRestAssuredCRUD.createStation("양재역"));
        강남역Id = extractResponseId(StationRestAssuredCRUD.createStation("강남역"));
        역삼역Id = extractResponseId(StationRestAssuredCRUD.createStation("역삼역"));
    }

    void setOrangeLine() {
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.createLine("3호선", "bg-orange-600");
        Long 삼호선Id = lineResponse.jsonPath().getLong("id");

        SectionRestAssuredCRUD.addSection(교대역Id, 남부터미널역Id, 교대역_남부터미널역_거리, 삼호선Id);
        SectionRestAssuredCRUD.addSection(남부터미널역Id, 양재역Id, 남부터미널역_양재역_거리, 삼호선Id);
    }

    void setGreenLine() {
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.createLine("2호선", "bg-green-600");
        Long 이호선Id = lineResponse.jsonPath().getLong("id");

        SectionRestAssuredCRUD.addSection(교대역Id, 강남역Id, 교대역_강남역_거리, 이호선Id);
        SectionRestAssuredCRUD.addSection(강남역Id, 역삼역Id, 강남역_역삼역_거리, 이호선Id);
    }

    void setRedLine() {
        ExtractableResponse<Response> lineResponse = LineRestAssuredCRUD.createLine("신분당선", "bg-red-600");
        Long 신분당선Id = lineResponse.jsonPath().getLong("id");

        SectionRestAssuredCRUD.addSection(강남역Id, 양재역Id, 양재역_강남역_거리, 신분당선Id);
    }

    /**
     * 교대역    --- *2호선* (1) ---   강남역  --- *2호선* (5) --- 역삼역
     * |                              |
     * *3호선*(3)                    *신분당선*(2)
     * |                              |
     * 남부터미널역  --- *3호선*(4) ---  양재역
     *
     */

    /**
     * given 하나의 노선에 구간을 등록하고
     * when 두 역 간의 경로를 조회하면
     * then 두 역 사이에 존재하는 역들을 조회할 수 있다.
     */
    @Test
    @DisplayName("환승역이 없는 하나의 노선에서 경로를 조회한다.")
    void findOneLinePath() {
        //given
        setOrangeLine();

        //when
        ExtractableResponse<Response> response = PathRestAssuredCRUD.showPath(교대역Id, 양재역Id);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<Long> stationIds = response.jsonPath().getList("stations.id", Long.class);
        double distance = response.jsonPath().getDouble("distance"); //PathResponse에는 int로 설정했음에도 응답값은 double 형식으로 리턴..

        assertThat(stationIds).containsExactly(교대역Id, 남부터미널역Id, 양재역Id);
        assertThat(distance).isEqualTo(7.0);
    }

    /**
     * given 여러개의 노선에 환승역이 존재하는 구간을 등록하고
     * when 두 역 간의 경로를 조회하면
     * then 두 역 사이의 최단 경로에 있는 역들을 조회할 수 있다.
     */
    @Test
    @DisplayName("환승역을 가지는 노선에서 최단 경로를 조회한다.")
    void findTransferLinePath() {
        //given
        setOrangeLine();
        setGreenLine();
        setRedLine();

        //when
        ExtractableResponse<Response> response = PathRestAssuredCRUD.showPath(교대역Id, 양재역Id);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<Long> stationIds = response.jsonPath().getList("stations.id", Long.class);
        double distance = response.jsonPath().getDouble("distance"); //PathResponse에는 int로 설정했음에도 응답값은 double 형식으로 리턴..

        assertThat(stationIds).containsExactly(교대역Id, 강남역Id, 양재역Id);
        assertThat(distance).isEqualTo(3.0);
    }

    Long extractResponseId(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getLong("id");
    }
}
