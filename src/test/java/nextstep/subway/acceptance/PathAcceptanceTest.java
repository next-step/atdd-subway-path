package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.SectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회를 검증한다.")
public class PathAcceptanceTest extends AcceptanceTest{

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
		assertThat(경로조회.jsonPath().getList("stations")).hasSize(3);
		assertThat(경로조회.jsonPath().getList("stations.name")).containsExactly("종합운동장역", "잠실역", "천호역");
		assertThat(경로조회.jsonPath().getInt("distance")).isEqualTo(20);
	}

	private ExtractableResponse<Response> 경로를_조회한다(long 종합운동장역, long 천호역) {
		return RestAssured.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when().get("/paths?source={source}&target={target}", 종합운동장역, 천호역)
				.then().log().all()
				.extract();
	}

	private void 노선에_구간을_추가한다(long 이호선, long 종합운동장역, long 잠실역, int distance) {
		지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(종합운동장역, 잠실역, distance));
	}

	private long 지하철노선을_생성한다(String name, String color) {
		return 지하철_노선_생성_요청(name, color).jsonPath().getLong("id");
	}

	private long 지하철역을_생성한다(String name) {
		return 지하철역_생성_요청(name).jsonPath().getLong("id");
	}
}
