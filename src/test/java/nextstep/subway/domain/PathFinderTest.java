package nextstep.subway.domain;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.applicaion.dto.PathResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("경로 찾기 클래스 테스트")
class PathFinderTest {
	/**
	 *              10
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선* 2                 *신분당선*  10
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 *                 3
	 */
	private Station 교대역;
	private Station 강남역;
	private Station 양재역;
	private Station 남부터미널역;

	private Line 이호선;
	private Line 신분당선;
	private Line 삼호선;

	private PathFinder pathFinder;

	@BeforeEach
	void setUp() {
		교대역 = new Station("교대역");
		강남역 = new Station("강남역");
		양재역 = new Station("양재역");
		남부터미널역 = new Station("남부터미널역");

		이호선 = new Line("이호선", "green");
		신분당선 = new Line("신분당선", "red");
		삼호선 = new Line("삼호선", "orange");

		이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
		신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
		삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
		삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));

		pathFinder = PathFinder.of(List.of(이호선, 신분당선, 삼호선));
	}

	@DisplayName("교대역에서 강남역까지 경로찾기 테스트")
	@Test
	void searchPathFrom교대To강남Test() {

		//given //when
		PathResponse pathResponse = pathFinder.searchShortestPath(교대역, 강남역);

		//then
		assertAll(
			() -> assertThat(pathResponse.getStations()).hasSize(2),
			() -> assertThat(pathResponse.getDistance()).isEqualTo(10)
		);
	}

	@DisplayName("교대역에서 양재역까지 경로찾기 테스트")
	@Test
	void searchPathFrom교대To양재Test() throws Exception {

		//given //when
		PathResponse pathResponse = pathFinder.searchShortestPath(교대역, 양재역);

		//then
		assertAll(
			() -> assertThat(pathResponse.getStations()).hasSize(3),
			() -> assertThat(pathResponse.getDistance()).isEqualTo(5)
		);
	}

	@DisplayName("남부터미널역에서 강남역까지 경로찾기 테스트")
	@Test
	void searchPathFrom남부터미널To강남Test() {

		//given //when
		PathResponse pathResponse = pathFinder.searchShortestPath(남부터미널역, 강남역);

		//then
		assertAll(
			() -> assertThat(pathResponse.getStations()).hasSize(3),
			() -> assertThat(pathResponse.getDistance()).isEqualTo(12)
		);
	}
}
