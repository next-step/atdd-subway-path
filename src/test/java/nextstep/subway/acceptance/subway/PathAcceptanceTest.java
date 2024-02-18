package nextstep.subway.acceptance.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.subway.LineSteps.노선_생성_요청;
import static nextstep.subway.utils.subway.PathSteps.최단_경로_조회_요청;
import static nextstep.subway.utils.subway.SectionSteps.구간_생성_요청;
import static nextstep.subway.utils.subway.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {
	private Long 일호선;

	@BeforeEach
	void createLine() {
		// given
		일호선 = 노선_생성_요청("1호선", "파랑", 동대문역, 종로3가역, 6).jsonPath().getLong("id");
	}

	/**
	 * Scenario: 경로가 단건일 경우, 조회 성공
	 * Given 해당 지하철 노선은
	 *       상행역 : 동대문역 / 하행역 : 종로3가역 / 길이 : 6
	 *       상행역 : 종로3가역 / 하행역 : 서울역 / 길이 : 10 인 구간들이 존재하는 노선을 생성한다.
	 * When 서울역부터 동대문역까지의 경로를 조회하면
	 * Then 경로에 있는 역 목록은 동대문역, 종로3가역, 서울역 순서대로 구성된다.
	 * Then 구간의 거리는 16이다.
	 */
	@DisplayName("단건인 경로를 조회한다.")
	@Test
	void 단건인_경로_조회() {
		// given
		구간_생성_요청(서울역, 종로3가역, 10, 일호선);

		// when
		ExtractableResponse<Response> response = 최단_경로_조회_요청(서울역, 동대문역);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id"))
				.containsExactly(서울역, 종로3가역, 동대문역);
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(16);
	}

	/**
	 * Scenario: 경로가 다건일 경우, 최단 경로 조회 성공
	 * Given 상행역 : 동대문역 / 하행역 : 종로3가역 / 길이 : 6
	 *       상행역 : 종로3가역 / 하행역 : 서울역 / 길이 : 10 인 구간들이 존재하는 노선을 생성한다.
	 * Given 상행역 : 동대문역 / 하행역 : 충무로역 / 길이 : 4
	 *       상행역 : 충무로역 / 하행역 : 서울역 / 길이 : 8 인 구간들이 존재하는 노선을 생성한다.
	 * When 서울역부터 동대문역까지의 경로를 조회하면
	 * Then 경로에 있는 역 목록은 서울역, 충무로, 동대문역 순서대로 구성된다.
	 * Then 구간의 거리는 12이다.
	 */
	@DisplayName("다건인 경로 중 최단 경로를 조회한다.")
	@Test
	void 다건인_경로_중_최단_경로_조회() {
		// given
		구간_생성_요청(서울역, 종로3가역, 10, 일호선);

		Long 사호선 = 노선_생성_요청("4호선", "하늘", 동대문역, 충무로역, 4).jsonPath().getLong("id");
		구간_생성_요청(서울역, 충무로역, 8, 사호선);

		// when
		ExtractableResponse<Response> response = 최단_경로_조회_요청(서울역, 동대문역);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id"))
				.containsExactly(서울역, 충무로역, 동대문역);
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(12);
	}

	/**
	 * Scenario: 노선이 다른 경로 조회 성공
	 * Given 상행역 : 동대문역 / 하행역 : 종로3가역 / 길이 : 6 구간이 존재하는 노선을 생성한다.
	 * Given 상행역 : 동대문역 / 하행역 : 충무로역 / 길이 : 4 구간이 존재하는 노선을 생성한다.
	 * When 종로3가역부터 충무로역까지의 경로를 조회하면
	 * Then 경로에 있는 역 목록은 종로3가역, 동대문역, 충무로역 순서대로 구성된다.
	 * Then 구간의 거리는 10이다.
	 */
	@DisplayName("노선이 다른 경로 조회.")
	@Test
	void 노선이_다른_경로_조회() {
		// given
		Long 사호선 = 노선_생성_요청("4호선", "하늘", 동대문역, 충무로역, 4).jsonPath().getLong("id");

		// when
		ExtractableResponse<Response> response = 최단_경로_조회_요청(종로3가역, 충무로역);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id"))
				.containsExactly(종로3가역, 동대문역, 충무로역);
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(10);
	}

	/**
	 * Scenario: 경로가 없으면 조회 실패
	 * Given 상행역 : 동대문역 / 하행역 : 종로3가역 / 길이 : 6 구간들이 존재하는 노선을 생성한다.
	 * Given 상행역 : 종로5가역 / 하행역 : 종각역 / 길이 : 3 구간이 존재하는 노선을 생성한다.
	 * When 종로3가부터 종각역까지의 경로를 조회하면
	 * Then "경로가 존재하지 않습니다."라는 메시지를 반환한다.
	 */
	@DisplayName("경로가 존재하지 않으 조회 실패.")
	@Test
	void 경로가_존재하지_않으면_조회_실패() {
		// given
		노선_생성_요청("4호선", "하늘", 충무로역, 서울역, 8).jsonPath().getLong("id");

		// when & then
		실패시_코드값_메시지_검증(최단_경로_조회_요청(종로3가역, 충무로역), HttpStatus.BAD_REQUEST.value(),"경로가 존재하지 않습니다.");
	}

	/**
	 * Scenario: 출발역이나 도착역이 존재하지 않으면 조회 실패
	 * Given 상행역 : 동대문역 / 하행역 : 종로3가역 / 길이 : 6 인 구간들이 존재하는 노선을 생성한다.
	 * When 종로5가부터 동대문역까지의 경로를 조회하면
	 * Then "출발역이 존재하지 않습니다."라는 메시지를 반환한다.
	 * When 종로3가부터 시청역까지의 경로를 조회하면
	 * Then "도착역이 존재하지 않습니다."라는 메시지를 반환한다.
	 */
	@DisplayName("출발역이나 도착역이 존재하지 않으면 조회 실패")
	@Test
	void 출발역_도착역_존재하지_않으면_조회_실패() {
		// when & then
		실패시_코드값_메시지_검증(최단_경로_조회_요청(종로5가역, 동대문역), HttpStatus.BAD_REQUEST.value(),"출발역이 존재하지 않습니다.");
		실패시_코드값_메시지_검증(최단_경로_조회_요청(종로3가역, 시청역), HttpStatus.BAD_REQUEST.value(),"도착역이 존재하지 않습니다.");
	}

	/**
	 * Scenario: 출발역과 도착역이 같으면 조회 실패
	 * Given 상행역 : 동대문역 / 하행역 : 종로3가역 / 길이 : 6
	 * When 종로3가부터 종로3가역까지의 경로를 조회하면
	 * Then "출발역과 도착역이 같을 수 없습니다."라는 메시지를 반환한다.
	 */
	@DisplayName("출발역과 도착역이 같으면 조회 실패")
	@Test
	void 출발역_도착역_같으면_조회_실패() {
		// when & then
		실패시_코드값_메시지_검증(최단_경로_조회_요청(종로3가역, 종로3가역), HttpStatus.BAD_REQUEST.value(),"출발역과 도착역이 같을 수 없습니다.");
	}

	private void 실패시_코드값_메시지_검증(ExtractableResponse<Response> response, int statusCode, String message) {
		assertThat(response.statusCode()).isEqualTo(statusCode);
		assertThat(response.body().jsonPath().getString("message")).isEqualTo(message);
	}
}
