package nextstep.subway.acceptance.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

import static nextstep.subway.utils.subway.LineSteps.노선_생성_요청;
import static nextstep.subway.utils.subway.PathSteps.최단_경로_조회_요청;
import static nextstep.subway.utils.subway.SectionSteps.구간_생성_요청;
import static nextstep.subway.utils.subway.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathAcceptanceTest {
	@BeforeAll
	void setup() {
		구간_생성_요청(수서역, 학여울역, 10, 분당선);
		구간_생성_요청(수서역, 개포동역, 8, 삼호선);
	}

	/**
	 * Scenario: 경로가 다건일 경우, 최단 경로 조회 성공
	 * Given 상행역 : 도곡역 / 하행역 : 학여울역 / 길이 : 6
	 *       상행역 : 학여울역 / 하행역 : 수서역 / 길이 : 10 인 구간들이 존재하는 노선을 생성한다.
	 * Given 상행역 : 도곡역 / 하행역 : 개포동역 / 길이 : 4
	 *       상행역 : 개포동역 / 하행역 : 수서역 / 길이 : 8 인 구간들이 존재하는 노선을 생성한다.
	 * When 수서역부터 도곡역까지의 경로를 조회하면
	 * Then 경로에 있는 역 목록은 수서역, 개포동, 도곡역 순서대로 구성된다.
	 * Then 구간의 거리는 12이다.
	 */
	@DisplayName("다건인 경로 중 최단 경로를 조회한다.")
	@Test
	void 최단_경로_조회() {
		// when & then
		최단_경로_조회_성공(최단_경로_조회_요청(수서역, 도곡역), 12, 수서역, 개포동역, 도곡역);
	}

	/**
	 * Scenario: 노선이 다른 경로 조회 성공
	 * Given 상행역 : 도곡역 / 하행역 : 학여울역 / 길이 : 6
	 *       상행역 : 학여울역 / 하행역 : 수서역 / 길이 : 10 인 구간들이 존재하는 노선을 생성한다.
	 * Given 상행역 : 도곡역 / 하행역 : 개포동역 / 길이 : 4
	 *       상행역 : 개포동역 / 하행역 : 수서역 / 길이 : 8 인 구간들이 존재하는 노선을 생성한다.
	 * When 학여울역부터 개포동역까지의 경로를 조회하면
	 * Then 경로에 있는 역 목록은 학여울역, 도곡역, 개포동역 순서대로 구성된다.
	 * Then 구간의 거리는 10이다.
	 */
	@DisplayName("노선이 다른 경로 조회.")
	@Test
	void 노선이_다른_경로_조회() {
		// when & then
		최단_경로_조회_성공(최단_경로_조회_요청(학여울역, 개포동역), 10, 학여울역, 도곡역, 개포동역);
	}

	/**
	 * Scenario: 경로가 없으면 조회 실패
	 * Given 상행역 : 도곡역 / 하행역 : 학여울역 / 길이 : 6 구간들이 존재하는 노선을 생성한다.
	 * Given 상행역 : 판교역 / 하행역 : 양재역 / 길이 : 3 구간이 존재하는 노선을 생성한다.
	 * When 학여울부터 양재역까지의 경로를 조회하면
	 * Then "경로가 존재하지 않습니다."라는 메시지를 반환한다.
	 */
	@DisplayName("경로가 존재하지 않으면 조회 실패.")
	@Test
	void 경로가_존재하지_않으면_조회_실패() {
		// given
		노선_생성_요청("1-1호선", "파랑", 판교역, 양재역, 8).jsonPath().getLong("id");

		// when & then
		실패시_코드값_메시지_검증(최단_경로_조회_요청(학여울역, 양재역), HttpStatus.BAD_REQUEST.value(),"경로가 존재하지 않습니다.");
	}

	/**
	 * Scenario: 출발역이나 도착역이 존재하지 않으면 조회 실패
	 * Given 상행역 : 도곡역 / 하행역 : 학여울역 / 길이 : 6 인 구간들이 존재하는 노선을 생성한다.
	 * When 수원역부터 도곡역까지의 경로를 조회하면
	 * Then "경로에 존재하지 않는 역입니다."라는 메시지를 반환한다.
	 */
	@DisplayName("출발역이나 도착역이 존재하지 않으면 조회 실패")
	@Test
	void 역이_존재하지_않으면_조회_실패() {
		// when & then
		실패시_코드값_메시지_검증(최단_경로_조회_요청(수원역, 도곡역), HttpStatus.BAD_REQUEST.value(),"경로에 존재하지 않는 역입니다.");
	}

	/**
	 * Scenario: 출발역과 도착역이 같으면 조회 실패
	 * Given 상행역 : 도곡역 / 하행역 : 학여울역 / 길이 : 6
	 * When 학여울부터 학여울역까지의 경로를 조회하면
	 * Then "출발역과 도착역이 같을 수 없습니다."라는 메시지를 반환한다.
	 */
	@DisplayName("출발역과 도착역이 같으면 조회 실패")
	@Test
	void 출발역_도착역_같으면_조회_실패() {
		// when & then
		실패시_코드값_메시지_검증(최단_경로_조회_요청(학여울역, 학여울역), HttpStatus.BAD_REQUEST.value(),"출발역과 도착역이 같을 수 없습니다.");
	}

	private void 최단_경로_조회_성공(ExtractableResponse<Response> response, int distance, Long... stationIds) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id"))
				.isEqualTo(Arrays.stream(stationIds)
						.map(Long::intValue)
						.collect(Collectors.toList()));
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(distance);
	}

	private void 실패시_코드값_메시지_검증(ExtractableResponse<Response> response, int statusCode, String message) {
		assertThat(response.statusCode()).isEqualTo(statusCode);
		assertThat(response.body().jsonPath().getString("message")).isEqualTo(message);
	}
}
