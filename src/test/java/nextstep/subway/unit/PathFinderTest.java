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
	private Station 교대역;
	private Station 강남역;
	private Station 양재역;
	private Station 남부터미널역;
	private Line 삼호선;
	private Line 신분당선;
	private Line 이호선;

	@BeforeEach
	void setUp() {
		pathFinder = new JGraphPathFinder();
		교대역 = new Station(1L, "교대역");
		강남역 = new Station(2L, "강남역");
		양재역 = new Station(3L, "양재역");
		남부터미널역 = new Station(4L, "남부터미널역");
		삼호선 = new Line(1L, "이호선", "green");;
		신분당선 = new Line(2L, "삼호선", "yellow");;
		이호선 = new Line(3L, "신분당선", "red");;
		이호선.addSection(교대역, 강남역, 10);
		신분당선.addSection(강남역, 양재역, 10);
		삼호선.addSection(교대역, 남부터미널역, 2);
		삼호선.addSection(남부터미널역, 양재역, 3);
	}

	@DisplayName("경로를 탐색한다.")
	@Test
	void findPath() {
		//given
		//when
		PathResponse path = pathFinder.find(List.of(이호선, 삼호선, 신분당선), 교대역, 양재역);

		//then
		assertThat(path.getDistance()).isEqualTo(5);
		assertThat(path.getStationIds()).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
	}

	@DisplayName("연결되지 않은 경로를 탐색하면 실패한다.")
	@Test
	void findPathSetNotConnectedPath() {;
		final Station 서울역 = new Station(5L, "수원역");
		final Station 용산역 = new Station(6L, "망포역");
		final Line 일호선 = new Line(4L, "일호선", "blue");

		일호선.addSection(서울역, 용산역, 10);

		//when then
		assertThatThrownBy(() -> pathFinder.find(List.of(이호선, 삼호선, 신분당선), 서울역, 양재역))
				.isInstanceOf(Exception.class);
	}

	@DisplayName("존재하지 않은 역의 경로를 탐색하면 실패한다.")
	@Test
	void findPathSetNotExistStation() {;
		final Station 안드로메다역 = new Station();
		final Station 깐따삐야역 = new Station();
		//when then
		assertThatThrownBy(() -> pathFinder.find(List.of(이호선, 삼호선, 신분당선), 안드로메다역, 깐따삐야역))
				.isInstanceOf(Exception.class);
	}
}
