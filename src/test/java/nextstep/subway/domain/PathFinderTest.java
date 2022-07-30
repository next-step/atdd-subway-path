package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}