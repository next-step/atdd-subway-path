package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.acceptance.station.StationFixture;
import nextstep.subway.line.dto.request.SaveLineRequestDto;
import nextstep.subway.line.dto.request.UpdateLineRequestDto;
import nextstep.subway.station.dto.request.SaveStationRequestDto;
import nextstep.subway.utils.AcceptanceTest;
import nextstep.subway.utils.DatabaseCleanup;
import nextstep.subway.utils.RestAssuredClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.UNDEFINED_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
public class LineAcceptanceTest {

    @LocalServerPort
    private int port;

    private static final String LINE_BASE_URL = Endpoint.LINE_BASE_URL.getUrl();

    private static final String LINE_ID_KEY = "id";

    private static final String LINE_NAME_KEY = "name";

    private Long 신사역_아이디;

    private Long 광교역_아이디;

    private Long 청량리역_아이디;

    private Long 춘천역_아이디;

    @Autowired
    private DatabaseCleanup databaseCleanUp;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanUp.execute();

        this.신사역_아이디 = saveStation(StationFixture.신사역);
        this.광교역_아이디 = saveStation(StationFixture.광교역);
        this.청량리역_아이디 = saveStation(StationFixture.청량리역);
        this.춘천역_아이디 = saveStation(StationFixture.춘천역);
    }

    /**
     * <pre>
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * </pre>
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        SaveLineRequestDto 신분당선 = 신분당선을_생성한다(신사역_아이디, 광교역_아이디);
        saveLine(신분당선);

        // then
        List<String> lineNames = findLinesAll()
                .jsonPath()
                .getList(LINE_NAME_KEY, String.class);

        assertAll(
                () -> assertThat(lineNames.size()).isEqualTo(1),
                () -> assertThat(lineNames).containsAnyOf(신분당선.getName())
        );
    }

    /**
     * <pre>
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     * </pre>
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void readLines() {
        // given
        SaveLineRequestDto 신분당선 = 신분당선을_생성한다(신사역_아이디, 광교역_아이디);
        SaveLineRequestDto 경춘선 = 경춘선을_생성한다(청량리역_아이디, 춘천역_아이디);
        Stream.of(신분당선, 경춘선)
                .forEach(this::saveLine);

        // when
        ExtractableResponse<Response> findLinesAllResponse = findLinesAll();
        List<String> lineNames = findLinesAllResponse
                .jsonPath()
                .getList(LINE_NAME_KEY, String.class);

        // then
        assertThat(lineNames)
                .containsOnly(신분당선.getName(), 경춘선.getName());
    }

    /**
     * <pre>
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * </pre>
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void readLine() {
        // given
        SaveLineRequestDto 경춘선 = 경춘선을_생성한다(청량리역_아이디, 춘천역_아이디);
        Long savedLineId = saveLine(경춘선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        // when
        String foundStationName = findLineById(savedLineId)
                .jsonPath()
                .getString(LINE_NAME_KEY);

        // then
        assertThat(foundStationName).isEqualTo(경춘선.getName());
    }

    /**
     * <pre>
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * </pre>
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        SaveLineRequestDto 신분당선 = 신분당선을_생성한다(신사역_아이디, 광교역_아이디);
        Long savedLineId = saveLine(신분당선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        // when
        String path = String.format("%s/%d", LINE_BASE_URL, savedLineId);
        ExtractableResponse<Response> updateStationResponse = RestAssuredClient.put(path, 수정한_신분당선);

        // then
        assertAll(
                () -> assertThat(updateStationResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> {
                    String updatedLine = findLineById(savedLineId)
                            .jsonPath()
                            .getString(LINE_NAME_KEY);

                    assertThat(updatedLine).isEqualTo(수정한_신분당선.getName());
                }
        );
    }

    /**
     * <pre>
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * </pre>
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        SaveLineRequestDto 경춘선 = 경춘선을_생성한다(청량리역_아이디, 춘천역_아이디);
        Long savedLineId = saveLine(경춘선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        // when
        String path = String.format("%s/%d", LINE_BASE_URL, savedLineId);
        ExtractableResponse<Response> deleteStationByIdResponse = RestAssuredClient.delete(path);

        // then
        assertAll(
                () -> assertThat(deleteStationByIdResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> {
                    List<String> LineNames = RestAssuredClient.get(LINE_BASE_URL)
                            .jsonPath()
                            .getList(LINE_NAME_KEY, String.class);

                    assertThat(LineNames).doesNotContain(경춘선.getName());
                }
        );
    }

    /**
     * <pre>
     * 지하철역을 생성하는 API를 호출하고
     * 저장된 지하철역의 id를 반환하는 함수
     * </pre>
     *
     * @param station
     * @return saved station id
     */
    private Long saveStation(SaveStationRequestDto station) {
        return RestAssuredClient.post(Endpoint.STATION_BASE_URL.getUrl(), station)
                .jsonPath()
                .getLong("id");
    }

    /**
     * <pre>
     * 지하철 노선을 생성하는 API를 호출하는 함수
     * </pre>
     *
     * @param line
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> saveLine(SaveLineRequestDto line) {
        ExtractableResponse<Response> saveLineResponse =
                RestAssuredClient.post(LINE_BASE_URL, line);
        assertThat(saveLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return saveLineResponse;
    }

    /**
     * <pre>
     * 모든 지하철 노선들을 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> findLinesAll() {
        ExtractableResponse<Response> findStationsAllResponse = RestAssuredClient.get(LINE_BASE_URL);
        assertThat(findStationsAllResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        return findStationsAllResponse;
    }

    /**
     * <pre>
     * 지하철 노선을 id로 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @param id
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> findLineById(Long id) {
        String path = String.format("%s/%d", LINE_BASE_URL, id);
        ExtractableResponse<Response> findStationByIdResponse = RestAssuredClient.get(path);
        assertThat(findStationByIdResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        return findStationByIdResponse;
    }

    private SaveLineRequestDto 신분당선을_생성한다(Long upStationId, Long downStationId) {
        return SaveLineRequestDto.builder()
                .name("신분당선")
                .color("#f5222d")
                .distance(15)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build();
    }

    private SaveLineRequestDto 경춘선을_생성한다(Long upStationId, Long downStationId) {
        return SaveLineRequestDto.builder()
                .name("경춘선")
                .color("#13c2c2")
                .distance(25)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build();
    }

    private final UpdateLineRequestDto 수정한_신분당선 = UpdateLineRequestDto.builder()
                    .name("수정한 신분당선")
                    .color("#cf1322")
                    .build();

}
