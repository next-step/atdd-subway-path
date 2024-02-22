package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import nextstep.subway.station.StationRepository;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.acceptance.fixture.LineFixture.*;
import static nextstep.subway.acceptance.fixture.StationFixture.newStation;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @BeforeEach
    void setUp() {
        sectionRepository.deleteAllInBatch();
        lineRepository.deleteAllInBatch();
        stationRepository.deleteAllInBatch();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // given
        ExtractableResponse<Response> 강남역 = newStation("강남역");
        ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
        Long 강남역_ID = 강남역.jsonPath().getLong("id");
        Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = newLine(
                "2호선",
                "bg-green-999",
                강남역_ID,
                건대입구역_ID,
                10
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-green-999");
        assertThat(response.jsonPath().getList("stations")).hasSize(2);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_ID, 건대입구역_ID);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "건대입구역");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLinesTest() {
        // given
        ExtractableResponse<Response> 강남역 = newStation("강남역");
        ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
        ExtractableResponse<Response> 군자역 = newStation("군자역");
        Long 강남역_ID = 강남역.jsonPath().getLong("id");
        Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
        Long 군자역_ID = 군자역.jsonPath().getLong("id");

        ExtractableResponse<Response> 이호선 = newLine("2호선", "bg-green-999", 강남역_ID, 건대입구역_ID, 10);
        ExtractableResponse<Response> 칠호선 = newLine("7호선", "bg-orange-600", 건대입구역_ID, 군자역_ID, 20);
        Long 이호선_ID = 이호선.jsonPath().getLong("id");
        Long 칠호선_ID = 칠호선.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = loadLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(".")).hasSize(2);
        assertThat(response.jsonPath().getList("id", Long.class)).containsExactly(이호선_ID, 칠호선_ID);
        assertThat(response.jsonPath().getList("name", String.class)).containsExactly("2호선", "7호선");
        assertThat(response.jsonPath().getList("color", String.class)).containsExactly("bg-green-999", "bg-orange-600");
        assertThat(response.jsonPath().getList("stations", String.class)).hasSize(2);
        assertThat(response.jsonPath().getList("stations[0].id", Long.class)).containsExactly(강남역_ID, 건대입구역_ID);
        assertThat(response.jsonPath().getList("stations[0].name", String.class)).containsExactly("강남역", "건대입구역");
        assertThat(response.jsonPath().getList("stations[1].id", Long.class)).containsExactly(건대입구역_ID, 군자역_ID);
        assertThat(response.jsonPath().getList("stations[1].name", String.class)).containsExactly("건대입구역", "군자역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineTest() {
        // given
        ExtractableResponse<Response> 강남역 = newStation("강남역");
        ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
        Long 강남역_ID = 강남역.jsonPath().getLong("id");
        Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");

        ExtractableResponse<Response> 이호선 = newLine("2호선", "bg-green-999", 강남역_ID, 건대입구역_ID, 10);
        Long 이호선_ID = 이호선.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = loadLine(이호선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isEqualTo(이호선_ID);
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-green-999");
        assertThat(response.jsonPath().getList("stations")).hasSize(2);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_ID, 건대입구역_ID);
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "건대입구역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineTest() {
        // given
        ExtractableResponse<Response> 강남역 = newStation("강남역");
        ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
        Long 강남역_ID = 강남역.jsonPath().getLong("id");
        Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");

        ExtractableResponse<Response> 이호선 = newLine("2호선", "bg-green-999", 강남역_ID, 건대입구역_ID, 10);
        Long 이호선_ID = 이호선.jsonPath().getLong("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("color", "bg-blue-222");

        // when
        ExtractableResponse<Response> response = updateLine("3호선", "bg-blue-222", 이호선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> loadLine = loadLine(Long.valueOf(이호선_ID));
        assertThat(loadLine.jsonPath().getLong("id")).isEqualTo(이호선_ID);
        assertThat(loadLine.jsonPath().getString("name")).isEqualTo("3호선");
        assertThat(loadLine.jsonPath().getString("color")).isEqualTo("bg-blue-222");
        assertThat(loadLine.jsonPath().getList("stations")).hasSize(2);
        assertThat(loadLine.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역_ID, 건대입구역_ID);
        assertThat(loadLine.jsonPath().getList("stations.name", String.class)).containsExactly("강남역", "건대입구역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLineTest() {
        // given
        ExtractableResponse<Response> 강남역 = newStation("강남역");
        ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
        Long 강남역_ID = 강남역.jsonPath().getLong("id");
        Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");

        ExtractableResponse<Response> 이호선 = newLine("2호선", "bg-green-999", 강남역_ID, 건대입구역_ID, 10);
        Long 이호선_ID = 이호선.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = deleteLine(이호선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> loadLine = loadLine(Long.valueOf(이호선_ID));
        assertThat(loadLine.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간 등록")
    @Nested
    class AddSection {

        /**
         * Given 2개의 지하철 역(A, B)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 하행 종착지(B)에 추가로 지하철 구간(N-B)을 등록한다.
         * Then 새로운 지하철 구간이 등록된다. (A-N-B)
         */
        @DisplayName("지하철 노선의 중간에 신규 구간을 등록한다.")
        @Test
        void successTestMiddle() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    건대입구역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    구의역_ID,
                    건대입구역_ID,
                    5
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.jsonPath().getList("stations")).hasSize(3);
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(성수역_ID, 구의역_ID, 건대입구역_ID);
            assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("성수역", "구의역", "건대입구역");
        }

        /**
         * Given 2개의 지하철 역(A, B)이 10 거리로 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 하행 종착지(B)에 추가로 지하철 구간(N-B)을 10 거리로 등록한다.
         * Then 기존 지하철 구간보다 길이가 같거나 긴 구간은 등록할 수 없어 에러가 발생한다.
         */
        @DisplayName("지하철 노선의 하행 종착지에 추가하려는 구간의 길이가 기존 구간보다 길거나 같으면 에러가 발생한다.")
        @Test
        void invalidDistanceErrorTest() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    건대입구역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    구의역_ID,
                    건대입구역_ID,
                    10
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("기존 구간보다 길거나 같은 구간을 추가할 수 없습니다. 구간 길이: 10");
        }

        /**
         * Given 2개의 지하철 역(A, B)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 하행 종착지(B)에 추가로 지하철 구간(B-C)을 등록한다.
         * Then 새로운 지하철 구간이 등록된다. (A-B-C)
         */
        @DisplayName("지하철 노선의 마지막에 신규 구간을 등록한다.")
        @Test
        void successTest1() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    건대입구역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    건대입구역_ID,
                    구의역_ID,
                    10
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.jsonPath().getList("stations")).hasSize(3);
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(성수역_ID, 건대입구역_ID, 구의역_ID);
            assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("성수역", "건대입구역", "구의역");
        }

        /**
         * Given 2개의 지하철 역(A, B)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 시작지(A)에 추가로 지하철 구간(N-A)을 등록을 시도하면
         * Then 새로운 지하철 구간이 등록된다. (N-A-B)
         */
        @DisplayName("지하철 노선의 시작에 신규 구간을 등록한다.")
        @Test
        void successTest2() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");

            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    건대입구역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    구의역_ID,
                    성수역_ID,
                    10
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.jsonPath().getList("stations")).hasSize(3);
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(구의역_ID, 성수역_ID, 건대입구역_ID);
            assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("구의역", "성수역", "건대입구역");
        }

        /**
         * Given 2개의 지하철 역(A, B)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 하행 종착지(B)에 추가로 지하철 구간(A-B)을 등록을 시도하면
         * Then 추가 구간(A-B)의 양 역(A, B)이 이미 노선에 등록되어있어 에러가 발생한다.
         */
        @DisplayName("지하철 노선 구간에 이미 등록되어 있는 구간을 추가하려 하면 에러가 발생한다.")
        @Test
        void duplicateStationErrorTest() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    구의역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    성수역_ID,
                    구의역_ID,
                    10
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("주어진 구간은 이미 노선에 등록되어 있는 구간입니다. upStationId: " + 성수역_ID + ", downStationId: " + 구의역_ID);
        }

        /**
         * Given 2개의 지하철 역(A, B)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 하행 종착지(B)에 추가로 지하철 구간(C-D)을 등록을 시도하면
         * Then 추가 구간(C-D)의 연결점이 없기 때문에 에러가 발생한다.
         */
        @DisplayName("새로 추가하려는 구간의 상행역이 노선의 하행 종착역과 다른 역이라면 에러가 발생한다.")
        @Test
        void invalidUpStationErrorTest() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");
            ExtractableResponse<Response> 잠실역 = newStation("잠실역");
            Long 잠실역_ID = 잠실역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    건대입구역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    구의역_ID,
                    잠실역_ID,
                    10
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("새로운 구간을 추가할 수 있는 연결점이 없습니다. upStationId: " + 구의역_ID + ", downStationId: " + 잠실역_ID);
        }

        /**
         * Given 2개의 지하철 역(A, B)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * When 지하철 노선 하행 시작지(A)에 추가로 지하철 구간(N-B)을 등록을 시도하면
         * Then 구간 중간에 새로운 구간이 추가된다. (A-N-B)
         */
        @DisplayName("지하철 노선의 중간에 새로운 구간을 추가한다.")
        @Test
        void invalidDownStationErrorTest() {
            // given
            ExtractableResponse<Response> 성수역 = newStation("성수역");
            Long 성수역_ID = 성수역.jsonPath().getLong("id");
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    성수역_ID,
                    건대입구역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = addSection(
                    이호선_ID,
                    구의역_ID,
                    건대입구역_ID,
                    5
            );

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.jsonPath().getList("stations")).hasSize(3);
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(성수역_ID, 구의역_ID, 건대입구역_ID);
            assertThat(response.jsonPath().getList("stations.name", String.class)).containsExactly("성수역", "구의역", "건대입구역");
        }
    }

    @DisplayName("지하철 구간 제거")
    @Nested
    class RemoveSection {

        /**
         * Given 3개의 지하철 역(A, B, C)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * And 지하철 노선에 2개의 구간(A-B, B-C)이 등록되어 있다.
         * When 지하철 노선에서 하행 종착역(C)을 이용해서 구간(B-C)을 제거한다.
         * Then 지하철 노선에 남은 구간 목록은 1개(A-B)이다.
         */
        @DisplayName("지하철 노선에서 구간을 제거한다.")
        @Test
        void successTest() {
            // given
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");
            ExtractableResponse<Response> 강남역 = newStation("강남역");
            Long 강남역_ID = 강남역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    건대입구역_ID,
                    구의역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            ExtractableResponse<Response> 건대입구역_구의역_구간 = addSection(이호선_ID, 구의역_ID, 강남역_ID, 10);

            // when
            ExtractableResponse<Response> response = removeSection(이호선_ID, 강남역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            ExtractableResponse<Response> loadLine = loadLine(Long.valueOf(이호선_ID));
            assertThat(loadLine.jsonPath().getList("stations")).hasSize(2);
            assertThat(loadLine.jsonPath().getList("stations.id", Long.class)).containsExactly(건대입구역_ID, 구의역_ID);
            assertThat(loadLine.jsonPath().getList("stations.name", String.class)).containsExactly("건대입구역", "구의역");
        }

        /**
         * Given 3개의 지하철 역(A, B, C)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * And 지하철 노선에 2개의 구간(A-B, B-C)이 등록되어 있다.
         * When 지하철 노선에서 하행 종착역이 아닌 역(B)을 이용해서 구간을 제거한다.
         * Then 지하철 노선의 가장 마지막 구간이 아니므로 에러가 발생한다.
         */
        @DisplayName("지하철 노선에서 하행 종착역이 아닌 역을 이용해서 구간을 제거하면 에러가 발생한다.")
        @Test
        void invalidLastStationErrorTest() {
            // given
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");
            ExtractableResponse<Response> 강남역 = newStation("강남역");
            Long 강남역_ID = 강남역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    건대입구역_ID,
                    구의역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            ExtractableResponse<Response> 건대입구역_구의역_구간 = addSection(이호선_ID, 구의역_ID, 강남역_ID, 10);

            // when
            ExtractableResponse<Response> response = removeSection(이호선_ID, 구의역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("노선의 하행 종착역만 삭제할 수 있습니다. stationId: " + 구의역_ID);
        }

        /**
         * Given 3개의 지하철 역(A, B, C)이 등록되어 있다.
         * And 1개의 지하철 노선이 등록되어 있다.
         * And 지하철 노선에 1개의 구간(A-B)이 등록되어 있다.
         * When 지하철 노선에서 하행 종착역(B)을 이용해서 구간을 제거한다.
         * Then 지하철 노선에 구간이 1개뿐인 경우 구간을 제거할 수 없어 에러가 발생한다.
         */
        @DisplayName("지하철 노선에서 구간이 1개뿐인 경우 구간을 제거할 수 없어 에러가 발생한다.")
        @Test
        void invalidSectionSizeErrorTest() {
            // given
            ExtractableResponse<Response> 건대입구역 = newStation("건대입구역");
            Long 건대입구역_ID = 건대입구역.jsonPath().getLong("id");
            ExtractableResponse<Response> 구의역 = newStation("구의역");
            Long 구의역_ID = 구의역.jsonPath().getLong("id");

            ExtractableResponse<Response> 이호선 = newLine(
                    "2호선",
                    "bg-green-000",
                    건대입구역_ID,
                    구의역_ID,
                    10
            );
            Long 이호선_ID = 이호선.jsonPath().getLong("id");

            // when
            ExtractableResponse<Response> response = removeSection(이호선_ID, 구의역_ID);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.body().asString()).isEqualTo("노선에 남은 구간이 1개뿐이라 삭제할 수 없습니다.");
        }
    }
}
