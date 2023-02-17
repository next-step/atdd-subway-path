package nextstep.subway.unit;

import nextstep.subway.applicaion.JGraphPathFinder;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {
	private PathFinder pathFinder;

	@BeforeEach
	void setUp() {
		pathFinder = new JGraphPathFinder();
	}

	@DisplayName("경로를 탐색한다.")
	@Test
	void findPath() {
		//given
		final Station 교대역 = new Station(1L, "교대역");
		final Station 강남역 = new Station(2L, "강남역");
		final Station 양재역 = new Station(3L, "양재역");
		final Station 남부터미널역 = new Station(4L, "남부터미널역");
		final Line 삼호선 = new Line(1L, "이호선", "green");;
		final Line 신분당선 = new Line(2L, "삼호선", "yellow");;
		final Line 이호선 = new Line(3L, "신분당선", "red");;
		이호선.addSection(교대역, 강남역, 10);
		신분당선.addSection(강남역, 양재역, 10);
		삼호선.addSection(교대역, 남부터미널역, 2);
		삼호선.addSection(남부터미널역, 양재역, 3);

		//when
		PathResponse path = pathFinder.find(List.of(이호선, 삼호선, 신분당선), 교대역, 양재역);

		//then
		assertThat(path.getDistance()).isEqualTo(5);
		assertThat(path.getStationIds()).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
	}

	@DisplayName("연결되지 않은 경로를 탐색하면 실패한다.")
	@Test
	void findPathSetNotConnectedPath() {
		//given
		final Station 교대역 = new Station(1L, "교대역");
		final Station 강남역 = new Station(2L, "강남역");
		final Station 양재역 = new Station(3L, "양재역");
		final Station 남부터미널역 = new Station(4L, "남부터미널역");
		final Station 서울역 = new Station(5L, "수원역");
		final Station 용산역 = new Station(6L, "망포역");
		final Line 삼호선 = new Line(1L, "이호선", "green");
		final Line 신분당선 = new Line(2L, "삼호선", "yellow");
		final Line 이호선 = new Line(3L, "신분당선", "red");
		final Line 일호선 = new Line(4L, "일호선", "blue");

		이호선.addSection(교대역, 강남역, 10);
		신분당선.addSection(강남역, 양재역, 10);
		삼호선.addSection(교대역, 남부터미널역, 2);
		삼호선.addSection(남부터미널역, 양재역, 3);
		일호선.addSection(서울역, 용산역, 10);

		//when then
		assertThatThrownBy(() -> pathFinder.find(List.of(이호선, 삼호선, 신분당선), 서울역, 양재역))
				.isInstanceOf(Exception.class);
	}
}
