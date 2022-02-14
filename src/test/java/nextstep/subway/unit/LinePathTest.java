package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.LinePathException;
import nextstep.subway.domain.util.LinePath;

public class LinePathTest {
	private Station 교대역;
	private Station 강남역;
	private Station 양재역;
	private Station 남부터미널역;
	private Line 이호선;
	private Line 신분당선;
	private Line 삼호선;

	/**
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 */
	@BeforeEach
	void setUp() {
		교대역 = new Station(1L, "교대역");
		강남역 = new Station(2L, "강남역");
		양재역 = new Station(3L, "양재역");
		남부터미널역 = new Station(4L, "남부터미널역");

		이호선 = new Line(1L, "2호선", "green");
		이호선.addSection(교대역, 강남역, 10);

		신분당선 = new Line(2L, "신분당선", "red");
		신분당선.addSection(강남역, 양재역, 10);

		삼호선 = new Line(3L, "3호선", "orange");
		삼호선.addSection(교대역, 남부터미널역, 2);
		삼호선.addSection(남부터미널역, 양재역, 3);
	}

	@DisplayName("최단 경로 조회")
	@Test
	void 최단_경로_조회() {
		// when
		LinePath linePath = new LinePath(Arrays.asList(이호선, 신분당선, 삼호선));

		// then
		List<Station> stations = linePath.searchPath(교대역, 양재역);
		int distance = linePath.getWeight(교대역, 양재역);
		assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(distance).isEqualTo(5);
	}

	@DisplayName("출발역고 도착역이 같은 역 조회")
	@Test
	void 출발역과_도착역이_같은_경우() {
		// when
		LinePath linePath = new LinePath(Arrays.asList(이호선, 신분당선, 삼호선));

		// then
		assertThatThrownBy(()  -> linePath.searchPath(교대역, 교대역))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("연결되어 있지 않은 역을 조회")
	@Test
	void 연결되어_있지_않은_지하철_경로_조회() {
		// when
		Station 사당역 = new Station(5L, "사당역");
		LinePath linePath = new LinePath(Arrays.asList(이호선, 신분당선, 삼호선));

		// then
		assertThatThrownBy(()  -> linePath.searchPath(교대역, 사당역))
			.isInstanceOf(LinePathException.class);
	}
}
