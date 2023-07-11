package nextstep.subway.line;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.ApiTest;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.ModifyLineRequest;
import nextstep.subway.station.StationSteps;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.SubwayLineSteps.지하철노선등록요청;
import static nextstep.subway.line.SubwayLineSteps.지하철노선등록요청_생성;
import static nextstep.subway.line.SubwayLineSteps.지하철노선목록조회요청;
import static nextstep.subway.line.SubwayLineSteps.지하철노선삭제요청;
import static nextstep.subway.line.SubwayLineSteps.지하철노선수정요청;
import static nextstep.subway.line.SubwayLineSteps.지하철노선수정요청_생성;
import static nextstep.subway.line.SubwayLineSteps.지하철노선조회요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class SubwayLineAcceptanceTest extends ApiTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노션을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createSubwayLine() {
        // given
        String stationName1 = "오이도역";
        String stationName2 = "이수역";
        String name = "4호선";
        String color = "bg-blue-500";
        int distance = 50;
        Long upStationId = 역_생성(stationName1);
        Long downStationId = 역_생성(stationName2);
        CreateLineRequest subwayLineRequest = 지하철노선등록요청_생성(name, color, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = 지하철노선등록요청(subwayLineRequest);

        // then
        지하철_노성_생성_검증(stationName1, stationName2, distance, upStationId, downStationId, subwayLineRequest, response);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getSubwayLines() {
        // given
        String stationName1 = "오이도역";
        String stationName2 = "이수역";
        String stationName3 = "남성역";
        String subwayLineName1 = "4호선";
        String subwayLineName2 = "7호선";
        String color1 = "bg-blue-600";
        String color2 = "bg-red-600";
        int distance = 50;
        Long stationId1 = 역_생성(stationName1);
        Long stationId2 = 역_생성(stationName2);
        Long stationId3 = 역_생성(stationName3);
        Long 첫번째_지하철_노선_ID = 지하철_노선_생성(subwayLineName1, color1, distance, stationId1, stationId2);
        Long 두번째_지하철_노선_ID = 지하철_노선_생성(subwayLineName2, color2, distance, stationId2, stationId3);

        // when
        ExtractableResponse<Response> response = 지하철노선목록조회요청();

        // then
        지하철_노선_목록_조회_검증(첫번째_지하철_노선_ID, 두번째_지하철_노선_ID, stationName1, stationName2, stationName3, subwayLineName1, subwayLineName2, color1, color2, distance, stationId1, stationId2, stationId3, response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getSubwayLine() {
        // given
        String stationName1 = "오이도역";
        String stationName2 = "이수역";
        String subwayLineName = "4호선";
        String color = "bg-blue-600";
        int distance = 50;
        Long upStationId = 역_생성(stationName1);
        Long downStationId = 역_생성(stationName2);
        Long 지하철_노선_ID = 지하철_노선_생성(subwayLineName, color, distance, upStationId, downStationId);

        // when
        ExtractableResponse<Response> response = 지하철노선조회요청(지하철_노선_ID);

        // then
        지하철_노선_조회_검증(stationName1, stationName2, subwayLineName, color, distance, upStationId, downStationId, 지하철_노선_ID, response);
    }

    /**
     *Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifySubwayLine() {
        // given
        String stationName1 = "오이도역";
        String stationName2 = "이수역";
        String subwayLineName = "4호선";
        String modifysubwayLineName = "7호선";
        String color = "bg-blue-600";
        String modifyColor = "bg-red-500";
        int distance = 50;
        Long upStationId = 역_생성(stationName1);
        Long downStationId = 역_생성(stationName2);
        Long 지하철_노선_ID = 지하철_노선_생성(subwayLineName, color, distance, upStationId, downStationId);
        ModifyLineRequest request = 지하철노선수정요청_생성(modifysubwayLineName, modifyColor);

        // when
        ExtractableResponse<Response> response = 지하철노선수정요청(지하철_노선_ID, request);

        // then
        수정된_지하철_노선_검증(stationName1, stationName2, modifysubwayLineName, modifyColor, distance, upStationId, downStationId, 지하철_노선_ID, response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteSubwayLine() {
        // given
        String stationName1 = "오이도역";
        String stationName2 = "이수역";
        String subwayLineName = "4호선";
        String color = "bg-blue-600";
        int distance = 50;
        Long upStationId = 역_생성(stationName1);
        Long downStationId = 역_생성(stationName2);
        Long 지하철_노선_ID = 지하철_노선_생성(subwayLineName, color, distance, upStationId, downStationId);

        // when
        ExtractableResponse<Response> response = 지하철노선삭제요청(지하철_노선_ID);

        // then
        삭제된_지하철_노선_검증(지하철_노선_ID, response);
    }

    private static Long 역_생성(String stationName1) {
        return StationSteps.지하철생성요청(StationSteps.지하철생성요청_생성(stationName1)).jsonPath().getLong("id");
    }

    private void 지하철_노성_생성_검증(String stationName1, String stationName2, int distance, Long upStationId, Long downStationId, CreateLineRequest subwayLineRequest, ExtractableResponse<Response> response) {
        assertThat(upStationId).isEqualTo(1L);
        assertThat(downStationId).isEqualTo(2L);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(subwayLineRequest.getName());
        assertThat(response.jsonPath().getString("color")).isEqualTo(subwayLineRequest.getColor());
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(distance);
        assertThat(response.jsonPath().getList("stations", StationResponse.class)).hasSize(2)
                .extracting("id").containsExactlyInAnyOrder(upStationId, downStationId);
        assertThat(response.jsonPath().getList("stations", StationResponse.class)).hasSize(2)
                .extracting("name").containsExactlyInAnyOrder(stationName1, stationName2);
    }

    private void 지하철_노선_목록_조회_검증(Long subwayId1, Long subwayId2, String stationName1, String stationName2, String stationName3, String subwayLineName1, String subwayLineName2, String color1, String color2, int distance, Long stationId1, Long stationId2, Long stationId3, ExtractableResponse<Response> response) {
        assertThat(stationId1).isEqualTo(1L);
        assertThat(stationId2).isEqualTo(2L);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("id", Long.class)).hasSize(2).containsExactlyInAnyOrder(subwayId1, subwayId2);
        assertThat(response.jsonPath().getList("name")).hasSize(2).containsExactlyInAnyOrder(subwayLineName1, subwayLineName2);
        assertThat(response.jsonPath().getList("color")).hasSize(2).containsExactlyInAnyOrder(color1, color2);
        assertThat(response.jsonPath().getList("distance")).hasSize(2).containsExactlyInAnyOrder(distance, distance);
        assertThat(response.jsonPath().getList("stations[0].name")).hasSize(2)
                .containsExactlyInAnyOrder(stationName1, stationName2);
        assertThat(response.jsonPath().getList("stations[0].id", Long.class)).hasSize(2)
                .containsExactlyInAnyOrder(stationId1, stationId2);
        assertThat(response.jsonPath().getList("stations[1].name")).hasSize(2)
                .containsExactlyInAnyOrder(stationName2, stationName3);
        assertThat(response.jsonPath().getList("stations[1].id", Long.class)).hasSize(2)
                .containsExactlyInAnyOrder(stationId2, stationId3);
    }

    private Long 지하철_노선_생성(String subwayLineName, String color, int distance, Long upStationId, Long downStationId) {
        return 지하철노선등록요청(지하철노선등록요청_생성(subwayLineName, color, upStationId, downStationId, distance)).jsonPath().getLong("id");
    }

    private void 지하철_노선_조회_검증(String stationName1, String stationName2, String subwayLineName, String color, int distance, Long upStationId, Long downStationId, Long 지하철_노선_ID, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isEqualTo(지하철_노선_ID);
        assertThat(response.jsonPath().getString("name")).isEqualTo(subwayLineName);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(distance);
        assertThat(response.jsonPath().getList("stations", StationResponse.class)).hasSize(2)
                .extracting("name").containsExactlyInAnyOrder(stationName1, stationName2);
        assertThat(response.jsonPath().getList("stations", StationResponse.class)).hasSize(2)
                .extracting("id").containsExactlyInAnyOrder(upStationId, downStationId);
    }

    private void 수정된_지하철_노선_검증(String stationName1, String stationName2, String modifysubwayLineName, String modifyColor, int distance, Long upStationId, Long downStationId, Long 지하철_노선_ID, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 수정된_지하철_노선 = 지하철노선조회요청(지하철_노선_ID);
        지하철_노선_조회_검증(stationName1, stationName2, modifysubwayLineName, modifyColor, distance, upStationId, downStationId, 지하철_노선_ID, 수정된_지하철_노선);
    }

    private void 삭제된_지하철_노선_검증(Long 지하철_노선_ID, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> 삭제된_지하철_조회 = 지하철노선조회요청(지하철_노선_ID);
        assertThat(삭제된_지하철_조회.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
