package nextstep.subway.acceptance.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.subway.LineSteps;
import nextstep.subway.utils.subway.SectionSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/db/subwayTest.sql")
@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
	private static final Long 노선 = 1L;
	private static final Long 상행종점역 = 1L;
	private static final Long 처음_등록한_종점역 = 2L;
	private static final Long 두번째_등록한_종점역 = 3L;
	private static final Long 등록할_역 = 4L;
	private static final Long 하행종점역이_아닌_역 = 5L;

	@BeforeEach
	void createLine() {
		// given
		LineSteps.노선_생성_요청("역1", "색1", 상행종점역, 처음_등록한_종점역, 1);
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * When 생성한 지하철 노선의 하행 종점역부터 새로운 구간의 하행역을 등록하면
	 * Then 지하철 노선 조회 시, 새로운 하행 종점역을 확인할 수 있다.
	 */
	@DisplayName("지하철 노선에 구간을 등록한다.")
	@DirtiesContext
	@Test
	void createSectionTest() {
		// when
		ExtractableResponse<Response> response = SectionSteps.구간_생성_요청(두번째_등록한_종점역, 처음_등록한_종점역, 10, 노선);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(SectionSteps.구간_조회_요청(노선).jsonPath().getList("downStationId")).contains(두번째_등록한_종점역.intValue());
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * Given 생성한 지하철 노선에 구간을 등록한다.
	 * When 등록한 구간과 같은 구간을 등록하면
	 * Then 등록되지 않고 코드값 400 (Bad Request) 을 반환한다.
	 */
	@DisplayName("지하철 노선에 등록되어 있는 역을 등록하면 실패한다.")
	@DirtiesContext
	@Test
	void createSectionWithRegisteredStationThenFailTest() {
		// given
		SectionSteps.구간_생성_요청(두번째_등록한_종점역, 처음_등록한_종점역, 10, 노선);

		// when & then
		ExtractableResponse<Response> response = SectionSteps.구간_생성_요청(상행종점역, 두번째_등록한_종점역, 10, 노선);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(), "해당 노선에 1역이 이미 존재합니다.");
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * Given 지하철 노선에 구간을 등록한다.
	 * When 상행역이 해당 노선의 하행 종점역이 아닌 구간을 등록하면
	 * Then 등록되지 않고 코드값 400 (Bad Requet) 을 반환한다.
	 */
	@DisplayName("하행 종점역이 상행역이 아닌 구간을 등록하면 실패한다.")
	@DirtiesContext
	@Test
	void createSectionWithUpStationIsNotEndStationThenFailTest() {
		// given
		SectionSteps.구간_생성_요청(두번째_등록한_종점역, 처음_등록한_종점역, 10, 노선);

		// when
		ExtractableResponse<Response> response = SectionSteps.구간_생성_요청(등록할_역, 하행종점역이_아닌_역, 10, 노선);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(),"노선의 하행 종점역과 구간의 상행역은 같아야 합니다.");
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * Given 지하철 노선에 구간을 생성한다.
	 * When 해당 구간을 삭제하면
	 * Then 구간 목록 조회 시, 생성한 구간을 찾을 수 없다.
	 */
	@DisplayName("지하철 노선의 구간을 제거 한다.")
	@DirtiesContext
	@Test
	void deleteSectionTest() {
		// given
		SectionSteps.구간_생성_요청(두번째_등록한_종점역, 처음_등록한_종점역, 10, 노선);

		// when
		ExtractableResponse<Response> response = SectionSteps.구간_삭제_요청(Map.of("stationId", 두번째_등록한_종점역), 노선);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(SectionSteps.구간_조회_요청(노선).jsonPath().getList("downStationId")).doesNotContain(두번째_등록한_종점역.intValue());
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * Given 지하철 노선에 구간을 생성한다.
	 * When 해당 노선의 하행 종점역이 아닌 구간을 삭제하면
	 * Then 삭제되지 않고 코드값 400 (Bad Request) 을 반환한다.
	 */
	@DisplayName("지하철 노선의 하행 종점역이 아닌 구간을 제거하면 실패한다.")
	@DirtiesContext
	@Test
	void deleteSectionWithStationIsNotEndThenFailTest() {
		// given
		SectionSteps.구간_생성_요청(두번째_등록한_종점역, 처음_등록한_종점역, 10, 노선);

		// when
		ExtractableResponse<Response> response = SectionSteps.구간_삭제_요청(Map.of("stationId", 처음_등록한_종점역), 노선);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(),"노선의 하행 종점역만 제거할 수 있습니다.");
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * When 해당 노선에 상행 종점역과 하행 종점역만 있는 경우 해당 구간을 삭제하면
	 * Then 삭제되지 않고 코드값 400 (Bad Request) 을 반환한다.
	 */
	@DisplayName("상행 종점역과 하행 종점역만 있는 지하철 노선의 구간을 제거하면 실패한다.")
	@DirtiesContext
	@Test
	void deleteSectionWithLineHasOneSectionThenFailTest() {
		// when
		ExtractableResponse<Response> response = SectionSteps.구간_삭제_요청(Map.of("stationId", 2L), 1L);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(),"상행 종점역과 하행 종점역만 있는 노선입니다.");
	}

	private void 실패시_코드값_메시지_검증(ExtractableResponse<Response> response, int statusCode, String message) {
		assertThat(response.statusCode()).isEqualTo(statusCode);
		assertThat(response.body().jsonPath().getString("message")).isEqualTo(message);
	}
}
