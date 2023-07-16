package subway.acceptance.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.acceptance.AcceptanceTest;
import subway.acceptance.station.StationSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public Map<String, Long> stationsMap = new HashMap<>();

    @BeforeEach
    void addStations() {
        // "교대역", "강남역", "역삼역", "선릉역", "삼성역", "잠실역", "강변역", "건대역", "성수역", "왕십리역"
        stationsMap = StationSteps.기본_역_생성();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성 한다.")
    @Test
    void createLine() {
        // when
        var 이호선 = LineRequestGenerator.이호선_요청_만들기(getStationId("강남역"), getStationId("역삼역"));
        LineSteps.노선_생성_API(이호선);

        // then
        var retrieveLineResponse = LineSteps.노선_목록_조회_API();
        assertThat(retrieveLineResponse.jsonPath().getList("name", String.class)).containsAnyOf(이호선.get("name"));

    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회 한다.")
    @Test
    void retrieveLines() {
        // given
        var 일호선 = LineRequestGenerator.일호선_요청_만들기(getStationId("강남역"), getStationId("역삼역"));
        LineSteps.노선_생성_API(일호선);

        var 이호선 = LineRequestGenerator.이호선_요청_만들기(getStationId("삼성역"), getStationId("잠실역"));
        LineSteps.노선_생성_API(이호선);

        // when
        var retrieveLineResponse = LineSteps.노선_목록_조회_API();
        List<String> lineNames = retrieveLineResponse.jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).containsExactlyInAnyOrder("1호선", "2호선");


    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 하나를 조회 한다.")
    @Test
    void createLineAndRetrieve() {
        // given
        var 일호선 = LineRequestGenerator.일호선_요청_만들기(getStationId("강남역"), getStationId("역삼역"));
        var createLineResponse = LineSteps.노선_생성_API(일호선);
        final String createdLocation = createLineResponse.header("Location");

        // when
        var retrieveLineResponse = LineSteps.노선_조회_API(createdLocation);
        String lineName = retrieveLineResponse.jsonPath().get("name");

        // then
        assertThat(lineName).isEqualTo(일호선.get("name"));

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정 한다.")
    @Test
    void modifyLine() {
        // given
        var 일호선 = LineRequestGenerator.일호선_요청_만들기(getStationId("강남역"), getStationId("역삼역"));
        var createLineResponse = LineSteps.노선_생성_API(일호선);
        final String createdLocation = createLineResponse.header("Location");

        // when
        var 일호선_수정 = LineRequestGenerator.generateLineModifyRequest("1호선천안", "bg-blue-800");
        var modifyLineResponse = LineSteps.노선_수정_API(createdLocation, 일호선_수정);

        // then
        assertThat(modifyLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제 한다.")
    @Test
    void deleteLine() {
        // given
        var 일호선 = LineRequestGenerator.일호선_요청_만들기(getStationId("강남역"), getStationId("역삼역"));
        var createLineResponse = LineSteps.노선_생성_API(일호선);
        final String createdLocation = createLineResponse.header("Location");
        final Integer createdId = createLineResponse.body().jsonPath().get("id");

        // when
        var 역_제거 = StationSteps.역_제거_API(createdLocation);
        assertThat(역_제거.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        var 노선_목록 = LineSteps.노선_목록_조회_API();
        assertThat(노선_목록.body().jsonPath().getList("id")).doesNotContain(createdId);
    }

    private Long getStationId(String name) {
        return stationsMap.get(name);
    }
}
