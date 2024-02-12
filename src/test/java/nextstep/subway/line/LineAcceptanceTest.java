package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.E2ETestInitializer;
import nextstep.subway.utils.line.StationLineManager;
import nextstep.subway.utils.station.StationManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 노선 관련 기능")
public class LineAcceptanceTest extends E2ETestInitializer {

    public static final String FIRST_LINE_NAME = "신분당선";
    public static final String FIRST_LINE_COLOR = "bg-red-600";
    public static final String SECOND_LINE_NAME = "분당선";
    public static final String SECOND_LINE_COLOR = "bg-green-600";
    public static final long DISTANCE = 10L;

    private LineRequest saveLineRequest;
    private LineRequest saveAnotherLineRequest;
    private LineRequest updateLineRequest;

    private long savedFirstStationId;
    private long savedSecondStationId;
    private long savedThirdStationId;

    @BeforeEach
    void setUp() {
        savedFirstStationId = StationManager.save("지하철역").jsonPath().getLong("id");
        savedSecondStationId = StationManager.save("새로운지하철역").jsonPath().getLong("id");
        savedThirdStationId = StationManager.save("또다른지하철역").jsonPath().getLong("id");

        saveLineRequest = new LineRequest(FIRST_LINE_NAME, FIRST_LINE_COLOR, savedFirstStationId, savedSecondStationId, DISTANCE);
        saveAnotherLineRequest = new LineRequest(SECOND_LINE_NAME, SECOND_LINE_COLOR, savedFirstStationId, savedSecondStationId, DISTANCE);
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    // TODO: 지하철 노선 생성 인수 테스트 메서드 생성
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createStationLine() {
        // when
        ExtractableResponse<Response> response = StationLineManager.save(saveLineRequest);

        // then
        String result = response.jsonPath().getString("name");
        Assertions.assertThat(result).isEqualTo(saveLineRequest.getName());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    // TODO: 지하철 노선 목록 조회 인수 테스트 메서드 생성
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllStationLine() {
        // given

        StationLineManager.save(saveLineRequest);
        StationLineManager.save(saveAnotherLineRequest);

        // when
        ExtractableResponse<Response> response = StationLineManager.findAll();

        // then
        List<String> stations = response.jsonPath().getList("name", String.class);
        Assertions.assertThat(stations).containsExactly(saveLineRequest.getName(), saveAnotherLineRequest.getName());
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    // TODO: 지하철 노선 조회 테스트 메서드 생성
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findStationLine() {
        // given
        StationLineManager.save(saveLineRequest);

        // when
        ExtractableResponse<Response> response = StationLineManager.findById(savedFirstStationId);

        // then
        String result = response.jsonPath().getString("name");
        Assertions.assertThat(result).isEqualTo(saveLineRequest.getName());
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다
     */
    // TODO: 지하철 노선 수정 인수 테스트 메서드 생성
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateStationLine() {
        // given
        ExtractableResponse<Response> savedResponse = StationLineManager.save(saveLineRequest);
        long lineId = savedResponse.jsonPath().getLong("id");

        // when
        StationLineManager.update(lineId, SECOND_LINE_NAME, SECOND_LINE_COLOR);

        // then
        ExtractableResponse<Response> response = StationLineManager.findById(lineId);
        Assertions.assertThat(response.jsonPath().getString("name")).isEqualTo(SECOND_LINE_NAME);
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    // TODO: 지하철 노선 삭제 인수 테스트 메서드 생성
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteStationLine() {
        // given
        StationLineManager.save(saveLineRequest);

        // when
        StationLineManager.delete(savedFirstStationId);
    }
}
