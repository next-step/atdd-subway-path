package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SourceTargetEqualException;
import nextstep.subway.exception.StationNotExistsOnTheLineException;
import nextstep.subway.exception.StationsNotConnectedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("최단 경로 검색 단위 테스트")
class PathFinderTest {

	static Distance 교대역_남부터미널역_거리;
	static Distance 교대역_강남역_거리;
	static Distance 남부터미널역_양재역_거리;
	static Distance 강남역_양재역_거리;
	static Station 교대역;
	static Station 강남역;
	static Station 양재역;
	static Station 남부터미널역;
	static Line 이호선;
	static Line 신분당선;
	static Line 삼호선;


	/**
	 * 교대역    --- *2호선*(3) ---   강남역
	 * |                            |
	 * *3호선*(5)                   *신분당선* (2)
	 * |                           |
	 * 남부터미널역  --- *3호선*(1) ---  양재역
	 */
	@BeforeAll
	static void setUp() {
		교대역_남부터미널역_거리 = Distance.valueOf(5);
		교대역_강남역_거리 = Distance.valueOf(3);
		남부터미널역_양재역_거리 = Distance.valueOf(1);
		강남역_양재역_거리 = Distance.valueOf(2);

		교대역 = new Station("교대역");
		강남역 = new Station("강남역");
		양재역 = new Station("양재역");
		남부터미널역 = new Station("남부터미널역");
		이호선 = new Line("이호선", "yellow", 교대역, 강남역, 교대역_강남역_거리);
		신분당선 = new Line("신분당선", "green", 강남역, 양재역, 강남역_양재역_거리);
		삼호선 = new Line("삼호선", "red", 교대역, 남부터미널역, 교대역_남부터미널역_거리);

		Section 남부터미널역_양재역 = new Section(삼호선, 남부터미널역, 양재역, 남부터미널역_양재역_거리);
		삼호선.addSection(남부터미널역_양재역);
	}

	static Stream<Arguments> provideShortestPathParameters() {

		return Stream.of(
				Arguments.of(
						asList(이호선, 신분당선, 삼호선), 교대역, 강남역, asList(교대역, 강남역), 3
				),
				Arguments.of(
						asList(이호선, 신분당선, 삼호선), 교대역, 양재역, asList(교대역, 강남역, 양재역), 5
				),
				Arguments.of(
						asList(이호선, 신분당선, 삼호선), 강남역, 남부터미널역, asList(강남역, 양재역, 남부터미널역), 3
				)
		);
	}

	@DisplayName("최단 경로 검색")
	@ParameterizedTest
	@MethodSource("provideShortestPathParameters")
	void testShortestPath(List<Line> 모든_노선, Station 출발역, Station 도착역, List<Station> 최단경로_역_목록, int 최단경로거리) {
		// when
		ShortestPathResponse shortestPath = PathFinder.findShortestPath(모든_노선, 출발역, 도착역);

		// then
		assertThat(shortestPath.getStations()).containsExactlyElementsOf(최단경로_역_목록);
		assertThat(shortestPath.getDistance()).isEqualTo(최단경로거리);
	}


	/**
	 * 교대역    --- *2호선*(3) ---   강남역
	 * |                            |
	 * *3호선*(5)                   *신분당선* (2)
	 * |                           |
	 * 남부터미널역  --- *3호선*(1) ---  양재역       부산역 -- *남부노선(100)* -- 해운대역
	 */
	@DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우의 최단 경로 검색")
	@Test
	void testShortestPathWhenSourceTargetNotConnected() {
		// given
		Station 부산역 = new Station("부산역");
		Station 해운대역 = new Station("해운대역");
		Line 남부노선 = new Line("남부노선", "gold", 부산역, 해운대역, Distance.valueOf(100));

		// when
		assertThatThrownBy(() -> PathFinder.findShortestPath(asList(이호선, 신분당선, 삼호선, 남부노선), 교대역, 해운대역))
				.isInstanceOf(StationsNotConnectedException.class);
	}

	@DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우의 최단 경로 검색")
	@Test
	void testShortestPathWhenSourceIsNotExists() {
		Station 신촌역 = new Station("신촌역");
		assertThatThrownBy(() -> PathFinder.findShortestPath(asList(이호선, 신분당선, 삼호선), 교대역, 신촌역))
				.isInstanceOf(StationNotExistsOnTheLineException.class);
	}

	@DisplayName("출발역과 도착역이 같은 경우의 최단 경로 검색")
	@Test
	void testShortestPathWhenSourceTargetSame() {
		assertThatThrownBy(() -> PathFinder.findShortestPath(asList(이호선, 신분당선, 삼호선), 교대역, 교대역))
				.isInstanceOf(SourceTargetEqualException.class);
	}

}
