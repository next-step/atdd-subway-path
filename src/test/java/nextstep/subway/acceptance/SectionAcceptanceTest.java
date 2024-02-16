package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;
    private Long 이호선;

    /**
     * Given 지하철 노선을 생성하고
     */
    @BeforeEach
    void setUp() {
        강남역 = createStation("강남역");
        역삼역 = createStation("역삼역");
        선릉역 = createStation("선릉역");
        이호선 = createLine("2호선", "green", 강남역, 역삼역, 10L);
    }

    /**
     * When 지하철 노선 마지막에 구간을 등록하면
     * Then 지하철 노선 마지막에 구간이 등록된다.
     */
    @DisplayName("지하철 노선 마지막에 구간을 등록한다.")
    @Test
    void addEndSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 역삼역, 선릉역, 10L);
        String locationHeader = response.header("Location");

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).containsExactly(강남역, 역삼역, 선릉역);
    }

    /**
     * When 지하철 노선 가운데에 구간을 등록하면
     * Then 지하철 노선 가운데에 구간이 등록된다.
     */
    @DisplayName("지하철 노선에 역 추가시 노선 가운데에 추가 할 수 있다.")
    @Test
    void addMiddleSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 강남역, 선릉역, 9L);
        String locationHeader = response.header("Location");

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).containsExactly(강남역, 선릉역, 역삼역);
    }

    /**
     * When 지하철 노선 가운데에 다음 구간의 거리와 같거나 긴 구간을 등록하면
     * Then 에러를 반환한다.
     */
    @DisplayName("다음 구간의 거리와 같거나 긴 구간을 등록하면 에러를 반환한다.")
    @Test
    void validateSectionDistance() {
        // when
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 강남역, 선릉역, 11L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("중간 구간의 거리는 다음 구간보다 짧아야합니다.");
    }

    /**
     * When 지하철 노선 처음에 구간을 등록하면
     * Then 지하철 노선 처음에 구간이 등록된다.
     */
    @DisplayName("지하철 노선에 역 추가시 노선 처음에 추가 할 수 있다.")
    @Test
    void addStartSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 선릉역, 강남역, 10L);
        String locationHeader = response.header("Location");

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).containsExactly(선릉역, 강남역, 역삼역);
    }

    /**
     * When 이미 해당 노선에 등록되어있는 역을 등록하면
     * Then 에러를 반환한다.
     */
    @DisplayName("이미 해당 노선에 등록되어있는 역이면 에러를 반환한다.")
    @Test
    void validateDuplicateStation() {
        // when
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 역삼역, 강남역, 10L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("이미 등록되어있는 역입니다.");
    }

    /**
     * Given 마지막 구간을 생성하고
     * When 마지막 역을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("마지막 지하철역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteEndSection() {
        // given
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 역삼역, 선릉역, 10L);
        String locationHeader = response.header("Location");

        // when
        SectionSteps.deleteSection(이호선, 선릉역);

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).containsExactly(강남역, 역삼역);
    }

    /**
     * Given 마지막 구간을 생성하고
     * When 가운데 역을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("가운데 지하철역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteMiddleSection() {
        // given
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 역삼역, 선릉역, 10L);
        String locationHeader = response.header("Location");

        // when
        SectionSteps.deleteSection(이호선, 역삼역);

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).containsExactly(강남역, 선릉역);
    }

    /**
     * Given 마지막 구간을 생성하고
     * When 상행 종점 역을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("상행 종점 역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteStartSection() {
        // given
        ExtractableResponse<Response> response = SectionSteps.addSection(이호선, 역삼역, 선릉역, 10L);
        String locationHeader = response.header("Location");

        // when
        SectionSteps.deleteSection(이호선, 강남역);

        // then
        List<Long> lineStationIds = LineSteps.getLineStationIds(locationHeader);
        assertThat(lineStationIds).containsExactly(역삼역, 선릉역);
    }

    /**
     * When 구간이 1개인 경우 삭제하면
     * Then 에러를 반환한다.
     */
    @DisplayName("구간이 1개인 경우 삭제하면 에러를 반환한다.")
    @Test
    void validateLastSection() {
        // when
        ExtractableResponse<Response> response = SectionSteps.deleteSection(이호선, 역삼역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo("구간이 1개인 경우 역을 삭제할 수 없습니다.");
    }

    private static Long createStation(String name) {
        return StationSteps.createStation(name).jsonPath().getLong("id");
    }

    private static Long createLine(String name, String color, Long upStation, Long downStation, Long distance) {
        return LineSteps.createLine(name, color, upStation, downStation, distance).jsonPath().getLong("id");
    }
}
