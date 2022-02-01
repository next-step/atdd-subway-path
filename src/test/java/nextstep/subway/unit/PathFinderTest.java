package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("최단 경로 검색 단위 테스트")
class PathFinderTest {

	/**
	 * 교대역    --- *2호선*(3) ---   강남역
	 * |                            |
	 * *3호선*(5)                   *신분당선* (2)
	 * |                           |
	 * 남부터미널역  --- *3호선*(1) ---  양재역
	 */
	static Stream<Arguments> provideShortestPathParameters() {
		Distance 교대역_남부터미널역_거리 = Distance.valueOf(5);
		Distance 교대역_강남역_거리 = Distance.valueOf(3);
		Distance 남부터미널역_양재역_거리 = Distance.valueOf(1);
		Distance 강남역_양재역_거리 = Distance.valueOf(2);

		Station 교대역 = new Station("교대역");
		Station 강남역 = new Station("강남역");
		Station 양재역 = new Station("양재역");
		Station 남부터미널역 = new Station("남부터미널역");
		Line 이호선 = new Line("이호선", "yellow", 교대역, 강남역, 교대역_강남역_거리);
		Line 신분당선 = new Line("신분당선", "green", 강남역, 양재역, 강남역_양재역_거리);
		Line 삼호선 = new Line("삼호선", "red", 교대역, 남부터미널역, 교대역_남부터미널역_거리);

		Section 남부터미널역_양재역 = new Section(삼호선, 남부터미널역, 양재역, 남부터미널역_양재역_거리);
		삼호선.addSection(남부터미널역_양재역);


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
}
