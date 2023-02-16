package nextstep.subway.unit;

import static nextstep.subway.common.PathFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.SubwayPath;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.PathErrorCode;
import nextstep.subway.domain.exception.PathSearchException;

class PathFinderTest {

	private List<Line> lines;

	/**
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 */
	@BeforeEach
	void setUp() throws Exception {
		Line 이호선 = new Line(
			"2호선",
			"green",
			withId(교대역(), 교대역_ID),
			withId(강남역(), 강남역_ID),
			10
		);
		Line 삼호선 = new Line(
			"3호선",
			"orange",
			withId(교대역(), 교대역_ID),
			withId(남부터미널역(), 남부터미널역_ID),
			2
		);
		Line 신분당선 = new Line(
			"신분당선",
			"red",
			withId(강남역(), 강남역_ID),
			withId(양재역(), 양재역_ID),
			10
		);
		Line 사호선 = new Line(
			"4호선",
			"blue",
			withId(동대문역(), 동대문역_ID),
			withId(혜화역(), 혜화역_ID),
			5
		);

		this.lines = List.of(
			withId(이호선, 이호선_ID),
			withId(삼호선, 삼호선_ID),
			withId(신분당선, 신분당선_ID),
			withId(사호선, 사호선_ID)
		);

		삼호선.addSection(withId(남부터미널역(), 남부터미널역_ID), withId(양재역(), 양재역_ID), 3);
	}

	@DisplayName("교대역에서 양재역까지 최단 경로와 최단 거리를 얻어낸다")
	@Test
	void 교대역에서_양재역까지_최단_경로와_최단_거리를_얻어낸다() throws Exception {
		// given
		PathFinder pathFinder = new PathFinder(this.lines);

		// when
		SubwayPath subwayPath = pathFinder.findPath(withId(교대역(), 교대역_ID), withId(양재역(), 양재역_ID));

		// then
		assertAll(
			() -> assertThat(subwayPath.getMinimumDistance()).isEqualTo(5),
			() -> assertThat(subwayPath.getStations()).hasSize(3)
		);
	}

	@DisplayName("출발역과 도착역이 같은경우 예외가 발생한다")
	@Test
	void 출발역과_도착역이_같은경우_예외가_발생한다() throws Exception {
		// given
		PathFinder pathFinder = new PathFinder(this.lines);
		Station 양재역 = withId(양재역(), 양재역_ID);

		// when then
		assertThatThrownBy(() -> pathFinder.findPath(양재역, 양재역))
			.isInstanceOf(PathSearchException.class)
			.hasMessage(PathErrorCode.EQUAL_SEARCH_STATION.getMessage());
	}

	@DisplayName("출발역과 도착역이 연결이되어있지 않은경우 예외가 발생한다")
	@Test
	void 출발역과_도착역이_연결이되어있지_않은경우_예외가_발생한다() {
		// given
		PathFinder pathFinder = new PathFinder(this.lines);

		// when then
		assertThatThrownBy(() -> pathFinder.findPath(withId(교대역(), 교대역_ID), withId(동대문역(), 동대문역_ID)))
			.isInstanceOf(PathSearchException.class)
			.hasMessage(PathErrorCode.NOT_CONNECTION.getMessage());
	}
}
