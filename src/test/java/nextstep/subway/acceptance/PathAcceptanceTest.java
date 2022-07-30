package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.SectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("경로 조회를 검증한다.")
public class PathAcceptanceTest extends AcceptanceTest {

	private long 종합운동장역;
	private long 잠실역;
	private long 천호역;
	private long 석촌역;

	private long 이호선;
	private long 팔호선;

	@BeforeEach
	public void setUp() {
		super.setUp();
		/**
		 * 종합운동장 -2호선(10)- 잠실 -2호선(10)- 천호
		 *    \  			|
		 *	8호선(10)	8호선(10)
		 *    	\		   |
		 *        석촌
		 */

		종합운동장역 = 지하철역을_생성한다("종합운동장역");
		잠실역 = 지하철역을_생성한다("잠실역");
		석촌역 = 지하철역을_생성한다("석촌역");
		천호역 = 지하철역을_생성한다("천호역");

		이호선 = 지하철노선을_생성한다("2호선", "green");
		팔호선 = 지하철노선을_생성한다("8호선", "pink");

		노선에_구간을_추가한다(이호선, 종합운동장역, 잠실역, 10);
		노선에_구간을_추가한다(이호선, 잠실역, 천호역, 10);

		노선에_구간을_추가한다(팔호선, 종합운동장역, 석촌역, 10);
		노선에_구간을_추가한다(팔호선, 석촌역, 잠실역, 10);
		노선에_구간을_추가한다(팔호선, 잠실역, 천호역, 10);
	}

	/**
	 * When 출발역과 종점역이 주어지면
	 * Then 경로상에 포함된 역들과 총 거리의 합이 반환된다.
	 */
	@DisplayName("출발역과 종점역이 주어지면 경로에 포함된 역들이 반환된다.")
	@Test
	void pathFindTest() {
		//when
		ExtractableResponse<Response> 경로조회 = 경로를_조회한다(종합운동장역, 천호역);

		//then
		assertAll(
				() -> assertThat(경로조회.statusCode()).isEqualTo(HttpStatus.OK.value()),
				() -> assertThat(경로조회.jsonPath().getList("stations")).hasSize(3),
				() -> assertThat(경로조회.jsonPath().getList("stations.name")).containsExactly("종합운동장역", "잠실역", "천호역"),
				() -> assertThat(경로조회.jsonPath().getInt("distance")).isEqualTo(20)
		);
	}

	/**
	 * When 출발역과 종점역이 같으면
	 * Then 조회할 수 없다.
	 */
	@DisplayName("출발역과 종점역이 같으면 경로를 조회할 수 없다")
	@Test
	void pathFindFailOnSameStation() {
		//when
		ExtractableResponse<Response> 경로조회 = 경로를_조회한다(종합운동장역, 종합운동장역);

		//then
		assertThat(경로조회.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * Given 연결되어 있지 않은 노선을 추가한다.
	 * When 출발역과 종점역이 서로 연결되지 않은 채로 조회하면
	 * Then 조회할 수 없다.
	 */
	@DisplayName("출발역과 종점역이 연결되어 있지 않으면 경로를 조회할 수 없다")
	@Test
	void pathFindFailOnDisconnectedStations() {
		//given
		long 강남역 = 지하철역을_생성한다("강남역");
		long 판교역 = 지하철역을_생성한다("판교역");
		long 신분당선 = 지하철노선을_생성한다("신분당선", "red");
		노선에_구간을_추가한다(신분당선, 강남역, 판교역, 10);

		//when
		ExtractableResponse<Response> 경로조회 = 경로를_조회한다(종합운동장역, 강남역);

		//then
		assertThat(경로조회.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * Given 노선에 존재하지 않는 역을 등록한다.
	 * When 출발역과 종점역이 존재하지 않으면
	 * Then 조회할 수 없다.
	 */
	@DisplayName("출발역과 종점역이 등록되어 있지 않으면 조회할 수 없다.")
	@Test
	void pathFindFailOnNotRegisteredStations() {
		//given
		long 강남역 = 100L;
		long 판교역 = 101L;

		//when
		ExtractableResponse<Response> 경로조회_종합운동장역_강남역 = 경로를_조회한다(종합운동장역, 강남역);
		ExtractableResponse<Response> 경로조회_강남역_종합운동장역 = 경로를_조회한다(강남역, 종합운동장역);
		ExtractableResponse<Response> 경로조회_강남역_판교역 = 경로를_조회한다(강남역, 종합운동장역);

		//then
		assertAll(
				() -> assertThat(경로조회_종합운동장역_강남역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
				() -> assertThat(경로조회_강남역_종합운동장역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
				() -> assertThat(경로조회_강남역_판교역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	private ExtractableResponse<Response> 경로를_조회한다(long source, long target) {
		return RestAssured.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().get("/paths?source={source}&target={target}", source, target)
				.then().log().all()
				.extract();
	}

	private void 노선에_구간을_추가한다(long lineId, long upStationId, long downStationId, int distance) {
		지하철_노선에_지하철_구간_생성_요청(lineId, createSectionCreateParams(upStationId, downStationId, distance));
	}

	private long 지하철노선을_생성한다(String name, String color) {
		return 지하철_노선_생성_요청(name, color).jsonPath().getLong("id");
	}

	private long 지하철역을_생성한다(String name) {
		return 지하철역_생성_요청(name).jsonPath().getLong("id");
	}
}
