package nextstep.subway.acceptance.line;

import io.restassured.path.json.JsonPath;
import nextstep.subway.acceptance.common.CommonAcceptanceTest;
import nextstep.subway.acceptance.common.Constant;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.request.UpdateLineRequest;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.line.LineApiExtractableResponse.*;
import static nextstep.subway.acceptance.station.StationApiExtractableResponse.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends CommonAcceptanceTest {

    String 신분당선 = "신분당선";
    String 수인분당선 = "수인분당선";
    Long 강남역_ID;
    Long 신논현역_ID;
    Long 압구정로데오역_ID;
    Long 강남구청역_ID;
    String 빨간색 = Constant.빨간색;
    String 파란색 = Constant.파란색;
    String 노란색 = Constant.노란색;
    int 기본_역_간격 = Constant.기본_역_간격;

    @BeforeEach
    protected void setUp() {
        강남역_ID = createStation(CreateStationRequest.from("강남역")).jsonPath().getLong("stationId");
        신논현역_ID = createStation(CreateStationRequest.from("신논현역")).jsonPath().getLong("stationId");
        압구정로데오역_ID = createStation(CreateStationRequest.from("압구정로데오역")).jsonPath().getLong("stationId");
        강남구청역_ID = createStation(CreateStationRequest.from("강남구청역")).jsonPath().getLong("stationId");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선을_생성() {
        // when
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 강남역_ID, 신논현역_ID, 기본_역_간격);
        assertThat(createLine(신분당선_생성_정보).statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationsId =
                selectLines().jsonPath().getList("lines.name", String.class);
        assertThat(stationsId).contains(신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록을_조회() {
        // given
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 강남역_ID, 신논현역_ID, 기본_역_간격);
        createLine(신분당선_생성_정보);
        CreateLineRequest 수인분당선_생성_정보 = CreateLineRequest.of(수인분당선, 노란색, 압구정로데오역_ID, 강남구청역_ID, 기본_역_간격);
        createLine(수인분당선_생성_정보);

        // when & then
        List<String> lineNames =
                selectLines().jsonPath().getList("lines.name", String.class);
        assertThat(lineNames).contains(신분당선, 수인분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선을_조회() {
        // given
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 강남역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when & then
        String responseLineName = selectLine(신분당선_ID).jsonPath().get("name");
        assertThat(responseLineName).isEqualTo(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선을_수정() {
        // given
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 강남역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        UpdateLineRequest 신분당선_수정_정보 = UpdateLineRequest.of(파란색, 기본_역_간격);
        updateLine(신분당선_ID, 신분당선_수정_정보);

        // then
        JsonPath responseJsonPath = selectLine(신분당선_ID).jsonPath();
        assertThat((String) responseJsonPath.get("color")).isEqualTo(파란색);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선을_삭제() {
        // given
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 강남역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        deleteLine(신분당선_ID);

        // then
        assertThat(selectLine(신분당선_ID).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}
