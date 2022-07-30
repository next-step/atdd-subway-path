package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.exception.CannotFindPathWithSameStationException;
import nextstep.subway.exception.DisconnectedStationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("경로검색을 검증한다")
class PathFinderTest {

	private Station 종합운동장역;
	private Station 잠실역;
	private Station 석촌역;
	private Station 천호역;

	private Line 이호선;
	private Line 팔호선;

	private PathFinder pathFinder;

	@BeforeEach
	void setUp() {
		/**
		 * 종합운동장 -2호선(15)- 잠실 -2호선(10)- 천호
		 *    \  			|
		 *	8호선(10)	8호선(3)
		 *    	\		   |
		 *          석촌
		 */
		종합운동장역 = new Station("종합운동장역");
		석촌역 = new Station("석촌역");
		잠실역 = new Station("잠실역");
		천호역 = new Station("천호역");

		이호선 = new Line("2호선", "green");
		팔호선 = new Line("8호선", "pink");

		이호선.addSection(종합운동장역, 잠실역, 15);
		이호선.addSection(잠실역, 천호역, 10);
		팔호선.addSection(종합운동장역, 석촌역, 10);
		팔호선.addSection(석촌역, 잠실역, 3);

		pathFinder = new PathFinder(List.of(이호선, 팔호선));
	}

	/**
	 * When 경로를 찾으려고 시도하면
	 * Then 경로가 가장 짧은 역들의 목록과 거리의 합을 반환한다.
	 */
	@DisplayName("findPath를 검증한다")
	@Test
	void findPath() {
	    //when
		PathResponse 경로_조회_결과 = pathFinder.findPath(종합운동장역, 천호역);

		//then
		assertAll(
				() -> assertThat(경로_조회_결과.getStations()).containsExactly(종합운동장역, 석촌역, 잠실역, 천호역),
				() -> assertThat(경로_조회_결과.getDistance()).isEqualTo(23)
		);
	}

	/**
	 * When 출발역과 종점역이 같으면
	 * Then 조회할 수 없다.
	 */
	@DisplayName("출발역과 종점역이 같으면 경로를 조회할 수 없다")
	@Test
	void pathFindFailOnSameStation() {
		//when
		//then
		assertThatThrownBy(() -> pathFinder.findPath(종합운동장역, 종합운동장역))
				.isInstanceOf(CannotFindPathWithSameStationException.class);
	}

	/**
	 * Given 연결되어 있지 않은 노선을 추가한다.
	 * When 출발역과 종점역이 서로 연결되지 않은 채로 경로를 조회하면
	 * Then 조회할 수 없다.
	 */
	@DisplayName("출발역과 종점역이 연결되어 있지 않으면 경로를 조회할 수 없다")
	@Test
	void pathFindFailOnDisconnectedStations() {
		//given
		Station 강남역 = new Station("강남역");
		Station 판교역 = new Station("판교역");
		Line 신분당선 = new Line("신분당선", "red");
		신분당선.addSection(강남역, 판교역, 10);

		PathFinder finder = new PathFinder(List.of(신분당선, 이호선, 팔호선));
		//when
		//then
		assertThatThrownBy(() -> finder.findPath(종합운동장역, 강남역))
				.isInstanceOf(DisconnectedStationsException.class);
	}
}