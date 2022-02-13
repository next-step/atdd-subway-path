package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {
	private static final Station 홍대입구역 = Station.of("홍대입구역");
	private static final Station 합정역 = Station.of("합정역");
	private static final Station 당산역 = Station.of("당산역");
	private static final Station 김포공항역 = Station.of("김포공항역");

	private final Line 이호선 = Line.of("2호선", "green", 홍대입구역, 합정역, Distance.from(10));
	private final Line 구호선 = Line.of("9호선", "gold", 당산역, 김포공항역, Distance.from(20));
	private final Line 공항철도선 = Line.of("공항철도선", "blue", 김포공항역, 홍대입구역, Distance.from(20));

	static Stream<Arguments> variableStations() {
		return Stream.of(
				Arguments.of("합정역 -> 김포공항역", 합정역, 김포공항역, 3),
				Arguments.of("당산역 -> 홍대입구역", 당산역, 홍대입구역, 3),
				Arguments.of("합정역 -> 김포공항역", 합정역, 홍대입구역, 2)
		);
	}

	/**
	 * 합정역    --- *2호선* ---   홍대입구역
	 * |             10            |
	 * *2호선* 20              *공항철도선* 20
	 * |            20            |
	 * 당산역  --- *9호선* ---   김포공항역
	 */
	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(홍대입구역, "id", 1L);
		ReflectionTestUtils.setField(합정역, "id", 2L);
		ReflectionTestUtils.setField(당산역, "id", 3L);
		ReflectionTestUtils.setField(김포공항역, "id", 4L);

		이호선.addSection(합정역, 당산역, Distance.from(20));
		ReflectionTestUtils.setField(이호선, "id", 1L);
		ReflectionTestUtils.setField(이호선.getSections().get(0), "id", 1L);
		ReflectionTestUtils.setField(이호선.getSections().get(1), "id", 2L);

		ReflectionTestUtils.setField(구호선, "id", 2L);
		ReflectionTestUtils.setField(구호선.getSections().get(0), "id", 1L);

		ReflectionTestUtils.setField(공항철도선, "id", 3L);
		ReflectionTestUtils.setField(공항철도선.getSections().get(0), "id", 1L);
	}

	/**
	 * When 다익스트라를 통한 최단거리 조회를 요청한다.
	 * Then 최단 거리가 조회된다.
	 */
	@DisplayName("djkstraShortest를 사용한 최단거리 조회")
	@ParameterizedTest(name = "{index}: {0}")
	@MethodSource("variableStations")
	void findUseDjkstraShortestPath(String message, Station depart, Station arrival, int routeCount) {
		PathFinder pathFinder = PathFinder.from(Arrays.asList(이호선, 구호선, 공항철도선));
		List<Station> paths = pathFinder.findRoute(depart, arrival);

		assertThat(paths).hasSize(routeCount);
		assertThat(paths).startsWith(depart);
		assertThat(paths).endsWith(arrival);
	}
}
