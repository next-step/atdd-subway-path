package nextstep.subway.acceptance.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.utils.subway.LineSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.subway.LineSteps.노선_단건_조회_요청;
import static nextstep.subway.utils.subway.SectionSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(value = "/db/subwayTest.sql")
@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
	private static final Long 노선 = 1L;
	private static final Long 종로3가역 = 1L;
	private static final Long 시청역 = 2L;
	private static final Long 서울역 = 3L;
	private static final Long 종각역 = 4L;
	private static final Long 종로5가역 = 5L;
	private static final Long 동대문역 = 6L;
	private static final int 종로3가역_시청역_길이 = 6;

	@BeforeEach
	void createLine() {
		// given
		LineSteps.노선_생성_요청("1호선", "파랑", 종로3가역, 시청역, 종로3가역_시청역_길이);
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * When 생성한 지하철 노선의 하행 종점역부터 새로운 구간의 하행역을 등록하면
	 * Then 지하철 노선 조회 시, 새로운 하행 종점역을 확인할 수 있다.
	 * Then 노선 전체 길이가 등록한 구간의 길이만큼 늘어난다.
	 */
	@DisplayName("지하철 노선에 하행 종점역이 포함된 구간을 등록한다.")
	@DirtiesContext
	@Test
	void 하행_종점역_구간_등록() {
		// when
		ExtractableResponse<Response> response = 구간_생성_요청(서울역, 시청역, 10, 노선);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		LineResponse line = 노선_단건_조회_요청(노선).as(LineResponse.class);
		assertTrue(line.getStaions().stream()
				.anyMatch(station -> station.getId() == 서울역));
		assertThat(line.getDistance()).isEqualTo(종로3가역_시청역_길이 + 10);
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * Given 해당 지하철 노선은
	 *       상행역 : 종로3가역 / 하행역 : 시청역 / 길이 : 6 인 구간이 존재한다.
	 * When 생성한 지하철 노선에
	 *       상행역 : 종로3가역 / 하행역이 종각역 / 길이가 4 인 구간을 등록하면
	 * Then 지하철 노선 조회 시,
	 *       - 종로3가역 다음역은 종각역이고, 종각역 다음역은 시청역이다.
	 *       - 종각역과 시청역 구간의 길이 = 2 이다.
	 */
	@DisplayName("지하철 노선에 중간역이 포함된 구간을 등록한다.")
	@DirtiesContext
	@Test
	void 중간역_구간_등록() {
		// when
		ExtractableResponse<Response> response = 구간_생성_요청(종각역, 종로3가역, 4, 노선);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		List<SectionResponse> sections = 구간_조회_요청(노선).jsonPath().getList("", SectionResponse.class);
		assertTrue(sections.stream()
				.anyMatch(section -> 종로3가역.equals(section.getUpStationId()) && 종각역.equals(section.getDownStationId())));
		assertTrue(sections.stream()
				.anyMatch(section -> 종각역.equals(section.getUpStationId()) && 시청역.equals(section.getDownStationId()) && section.getDistance() == 2));
	}

	/**
	 * Given 종로3가역, 시청역 구간이 등록된 지하철 노선을 생성하고
	 * When 생성한 지하철 노선에 상행역이 종로5가역, 하행역이 종로3가역 명시된 구간을 등록하면
	 * Then 지하철 노선 조회 시,
	 *       - 상행종점역은 종로5가역이다.
	 *       - 노선 전체 길이가 등록한 구간의 길이만큼 늘어난다.
	 */
	@DisplayName("지하철 노선에 상행 종점역을 등록한다.")
	@DirtiesContext
	@Test
	void 상행_종점역_구간_등록() {
		// when
		ExtractableResponse<Response> response = 구간_생성_요청(종로3가역, 종로5가역, 1, 노선);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		LineResponse line = 노선_단건_조회_요청(노선).as(LineResponse.class);
		assertTrue(line.getStaions().stream()
				.anyMatch(station -> station.getId() == 종로5가역));
		assertThat(line.getDistance()).isEqualTo(종로3가역_시청역_길이 + 1);
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
	void 이미_등록된_역_구간_등록_시_실패() {
		// given
		구간_생성_요청(서울역, 시청역, 10, 노선);

		// when & then
		ExtractableResponse<Response> response = 구간_생성_요청(종로3가역, 서울역, 10, 노선);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(), "해당 노선에 등록할 역들이 이미 존재합니다.");
	}

	/**
	 * Given 종로3가역, 시청역 구간이 등록된 지하철 노선을 생성하고
	 * When 생성한 지하철 노선에 상행역이 동대문역, 하행역이 종로5가역으로 명시된 구간을 등록하면
	 * Then 등록되지 않고 코드값 400 (Bad Request) 을 반환한다.
	 */
	@DisplayName("모든 역들이 존재하지 않는 구간을 등록하면 실패한다.")
	@DirtiesContext
	@Test
	void 존재하지_않는_역만_있는_구간_등록_시_실패() {
		// when
		ExtractableResponse<Response> response = 구간_생성_요청(종로5가역, 동대문역, 10, 노선);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(),"등록 구간의 역들이 모두 노선에 존재하지 않습니다.");
	}

	/**
	 * Given 지하철 노선을 생성한다.
	 * Given 해당 지하철 노선은
	 * 		상행역 : 종로3가역 / 하행역 : 시청역 / 길이 : 6 인 구간이 존재한다.
	 * When 생성한 지하철 노선에
	 * 		상행역 : 종로3가역 / 하행역 : 종각역 / 길이가 6 이상인 구간을 등록하면
	 * Then 등록되지 않고 코드값 400 (Bad Request) 을 반환한다.
	 */
	@DisplayName("등록 구간의 길이는 기존 구간의 길이보다 크거나 같을 수 없다.")
	@DirtiesContext
	@Test
	void 기존_구간_길이_이상의_구간_등록_시_실패() {
		// when
		ExtractableResponse<Response> response = 구간_생성_요청(종각역, 종로3가역, 6, 노선);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(),"등록 구간의 길이는 기존 구간의 길이보다 크거나 같을 수 없습니다.");
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
		구간_생성_요청(서울역, 시청역, 10, 노선);

		// when
		ExtractableResponse<Response> response = 구간_삭제_요청(Map.of("stationId", 서울역), 노선);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
		assertThat(구간_조회_요청(노선).jsonPath().getList("downStationId")).doesNotContain(서울역.intValue());
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
		구간_생성_요청(서울역, 시청역, 10, 노선);

		// when
		ExtractableResponse<Response> response = 구간_삭제_요청(Map.of("stationId", 시청역), 노선);

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
		ExtractableResponse<Response> response = 구간_삭제_요청(Map.of("stationId", 2L), 1L);

		// then
		실패시_코드값_메시지_검증(response, HttpStatus.BAD_REQUEST.value(),"상행 종점역과 하행 종점역만 있는 노선입니다.");
	}

	private void 실패시_코드값_메시지_검증(ExtractableResponse<Response> response, int statusCode, String message) {
		assertThat(response.statusCode()).isEqualTo(statusCode);
		assertThat(response.body().jsonPath().getString("message")).isEqualTo(message);
	}
}
