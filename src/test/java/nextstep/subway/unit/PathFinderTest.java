package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ErrorMessage;
import nextstep.subway.exception.NotExistedPathException;
import nextstep.subway.exception.NotExistedStationException;
import nextstep.subway.exception.SameStationException;

class PathFinderTest {
	private static final Station 존재하지_않는_역 = new Station("존재하지 않는 역");
	private PathFinder pathFinder;
	private Line 신분당선;
	private Line 이호선;
	private Line 삼호선;
	private Line 구호선;
	private Line 칠호선;
	private Station 신사역;
	private Station 논현역;
	private Station 신논현역;
	private Station 강남역;
	private Station 양재역;
	private Station 고속터미널역;
	private Station 교대역;
	private Station 남부터미널역;
	private Station 어린이대공원역;
	private Station 건대입구역;

	/* Line
	  신사역 --- *신분당선* ---    논현역
	  |                        |
	  *3호선*                   *신분당선*
	  |                        |
	  고속터미널역  --- *9호선* --- 신논현역
	  |                        |
	  *3호선*                   *신분당선*
	  |                        |
	  교대역   --- *2호선* ---    강남역
	  |                        |
	  *3호선*                   *신분당선*
	  |                        |
	  남부터미널역  --- *3호선* --- 앙재역
	 */

	/** Distance
	 * 신사역 ---   10   ---    논현역
	 * |                        |
	 * 3                        5
	 * |                        |
	 * 고속터미널역  --- 9 ---    신논현역
	 * |                        |
	 * 5                        6
	 * |                        |
	 * 교대역   ---  2   ---    강남역
	 * |                        |
	 * 1                        7
	 * |                        |
	 * 남부터미널역  --- 3 ---    앙재역
	 */

	@BeforeEach
	public void setUp() {
		// Given
		강남역 = new Station("강남역");
		신사역 = new Station("신사역");
		양재역 = new Station("양재역");
		논현역 = new Station("논현역");
		고속터미널역 = new Station("고속터미널역");
		남부터미널역 = new Station("남부터미널역");
		신논현역 = new Station("신논현역");
		교대역 = new Station("교대역");
		어린이대공원역 = new Station("어린이대공원역");
		건대입구역 = new Station("건대입구역");

		신분당선 = new Line("신분당선", "red");
		이호선 = new Line("이호선", "green");
		삼호선 = new Line("삼호선", "orange");
		구호선 = new Line("구호선", "yellow");
		칠호선 = new Line("칠호선", "khaki");

		신분당선.addSection(신사역, 논현역, 10);
		신분당선.addSection(논현역, 신논현역, 5);
		신분당선.addSection(신논현역, 강남역, 6);
		신분당선.addSection(강남역, 양재역, 7);
		이호선.addSection(교대역, 강남역, 2);
		삼호선.addSection(신사역, 고속터미널역, 3);
		삼호선.addSection(고속터미널역, 교대역, 5);
		삼호선.addSection(교대역, 남부터미널역, 1);
		삼호선.addSection(남부터미널역, 양재역, 3);
		구호선.addSection(고속터미널역, 신논현역, 9);
		칠호선.addSection(어린이대공원역, 건대입구역, 7);

		pathFinder = PathFinder.of(List.of(신분당선, 이호선, 삼호선, 칠호선, 구호선));
	}

	@DisplayName("최단 경로 탐색")
	@Test
	void getShortestPath() {
		// When
		GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(남부터미널역, 강남역);

		// Then
		assertAll(
			() -> 경로_구간_검증(shortestPath.getVertexList(), List.of(남부터미널역, 교대역, 강남역)),
			() -> 경로_거리_검증((int)shortestPath.getWeight(), 3)
		);
	}

	@DisplayName("최단 경로 탐색 - 실패 - 출발역이 존재하지 않는 경우")
	@Test
	void getShortestPath_fail_WHEN_SOURCE_DOES_NOT_EXISTED_THEN_THROW_EXCEPTION() {
		// When & Then
		assertThatThrownBy(() -> pathFinder.getShortestPath(존재하지_않는_역, 강남역))
			.isInstanceOf(NotExistedStationException.class)
			.hasMessage(ErrorMessage.SHOULD_BE_PROVIDED_EXISTED_STATION.getMessage());
	}

	@DisplayName("최단 경로 탐색 - 실패 - 도착역이 존재하지 않는 경우")
	@Test
	void getShortestPath_fail_WHEN_TARGET_DOES_NOT_EXISTED_THEN_THROW_EXCEPTION() {
		// When & Then
		assertThatThrownBy(() -> pathFinder.getShortestPath(남부터미널역, 존재하지_않는_역))
			.isInstanceOf(NotExistedStationException.class)
			.hasMessage(ErrorMessage.SHOULD_BE_PROVIDED_EXISTED_STATION.getMessage());
	}

	@DisplayName("최단 경로 탐색 - 실패 - 출발역과 도착역이 같은 경우")
	@Test
	void getShortestPath_fail_WHEN_SOURCE_AND_TARGET_IS_SAME_THEN_THROW_EXCEPTION() {
		// When & Then
		assertThatThrownBy(() -> pathFinder.getShortestPath(강남역, 강남역))
			.isInstanceOf(SameStationException.class)
			.hasMessage(ErrorMessage.SHOULD_BE_DIFFERENT_SOURCE_AND_TARGET.getMessage());
	}

	@DisplayName("최단 경로 탐색 - 실패 - 경로가 존재하지 않는 경우")
	@Test
	void getShortestPath_fail_WHEN_PATH_DOES_NOT_EXISTED_THEN_THROW_EXCEPTION() {
		// When & Then
		assertThatThrownBy(() -> pathFinder.getShortestPath(남부터미널역, 건대입구역))
			.isInstanceOf(NotExistedPathException.class)
			.hasMessage(ErrorMessage.SHOULD_EXIST_PATH.getMessage());
	}

	private void 경로_구간_검증(List<Station> actual, List<Station> expected) {
		assertThat(actual).containsExactly(expected.toArray(new Station[expected.size()]));
	}

	private void 경로_거리_검증(int actual, int expected) {
		assertThat(actual).isEqualTo(expected);
	}
}
