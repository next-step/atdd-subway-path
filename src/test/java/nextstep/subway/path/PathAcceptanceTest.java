package nextstep.subway.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.controller.dto.LineCreateRequest;
import nextstep.subway.line.controller.dto.SectionAddRequest;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.helper.JsonPathUtils.*;
import static nextstep.subway.line.acceptance.LineApiRequester.createLine;
import static nextstep.subway.line.acceptance.SectionApiRequester.addSectionToLineSuccess;
import static nextstep.subway.path.PathApiRequester.getPaths;
import static nextstep.subway.station.acceptance.StationApiRequester.createStation;
import static nextstep.subway.station.acceptance.StationApiRequester.deleteStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 기능")
@AcceptanceTest
public class PathAcceptanceTest {

    /**
     *  강남역 -- 역삼역 -- 선릉역 (2호선)
     *   ||
     * 신논현역
     *   ||
     *  논현역
     * (신분당선)
     */
    // 2호선
    long 이호선;
    long 강남역;
    long 역삼역;
    long 선릉역;

    // 신분당선
    long 신분당선;
    long 신논현역;  // 강남-신논현 연결
    long 논현역;

    // 1호선
    long 일호선;
    long 수원역;
    long 화서역;

    long 없는역;

    @BeforeEach
    void setup() {
        // 역 생성
        강남역 = getLongPath(createStation("강남역"), "id");
        역삼역 = getLongPath(createStation("역삼역"), "id");
        선릉역 = getLongPath(createStation("선릉역"), "id");

        신논현역 = getLongPath(createStation("신논현역"), "id");
        논현역 = getLongPath(createStation("논현역"), "id");

        수원역 = getLongPath(createStation("수원역"), "id");
        화서역 = getLongPath(createStation("화서역"), "id");

        없는역 = getLongPath(createStation("없는역"), "id");
        deleteStation(없는역);

        // 노선생성
        이호선 = getLongPath(
            createLine(new LineCreateRequest("2호선", "green", 강남역, 역삼역, 3)),
            "id"
        );

        신분당선 = getLongPath(
            createLine(new LineCreateRequest("신분당선", "red", 신논현역, 논현역, 5)),
            "id"
        );

        일호선 = getLongPath(
            createLine(new LineCreateRequest("1호선", "blue", 수원역, 화서역, 5)),
            "id"
        );

        // 구간추가
        addSectionToLineSuccess(이호선, new SectionAddRequest(역삼역, 선릉역, 7));
        addSectionToLineSuccess(신분당선, new SectionAddRequest(강남역, 신논현역, 2));
    }

    /**
     * given 출발역과 도착역으로
     * when 경로를 조회하면
     * then 출발역으로부터 도착역까지 경로에 있는 역 목록을 조회할 수 있다
     */
    @DisplayName("출발역과 도착역이 주어지면 경로를 조회할 수 있다")
    @Test
    void getPath() {
        ExtractableResponse<Response> response = getPaths(강남역, 논현역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getListPath(response.body(), "stations.id", String.class)).isNotEmpty();
        assertThat(getListPath(response.body(), "stations.name", String.class))
            .contains("강남역", "신논현역", "논현역");
        assertThat(getIntPath(response.body(), "distance")).isEqualTo(7);
    }

    /**
     * given, when 출발역과 도착역이 동일하면
     * then 경로 조회에 실패한다
     */
    @DisplayName("주어진 출발역과 도착역이 같으면 경로를 조회할 수 없다")
    @Test
    void cannotGetPathIfEqualsSourceAndTarget() {
        ExtractableResponse<Response> response = getPaths(강남역, 강남역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 출발역과 도착역이 주어졌지만
     * when 출발역과 도착역이 연결되어있지 않으면
     * then 경로 조회에 실패한다
     */
    @DisplayName("주어진 출발역과 도착역이 같으면 경로를 조회할 수 없다")
    @Test
    void cannotGetPathIfNotConnected() {
        ExtractableResponse<Response> response = getPaths(강남역, 화서역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 주어진 출발역 혹은 도착역이
     * when 존재하지 않으면
     * then 경로 조회에 실패한다
     */
    @DisplayName("주어진 출발역과 도착역이 같으면 경로를 조회할 수 없다")
    @Test
    void cannotGetPathIfNotFound() {
        ExtractableResponse<Response> response = getPaths(강남역, 없는역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
