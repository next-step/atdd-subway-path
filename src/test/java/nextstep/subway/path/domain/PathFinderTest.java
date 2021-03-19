package nextstep.subway.path.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;

class PathFinderTest {

	@DisplayName("출발역부터 도착역까지의 최단 경로를 조회한다.")
	@Test
	void findShortestPath() {
		// given
		final Station 강남역 = new Station("강남역");
		final Station 남부터미널역 = new Station("남부터미널역");
		final Station 양재역 = new Station("양재역");
		final Station 교대역 = new Station("교대역");

		final List<Section> sections = Arrays.asList(
			Section.of(null, 강남역, 양재역, 10),
			Section.of(null, 교대역, 강남역, 10),
			Section.of(null, 교대역, 남부터미널역, 3));

		// when
		PathFinder finder = PathFinder.of(sections);
		final List<Station> stations = finder.findShortestPath(강남역, 남부터미널역);
		final int distance = finder.findShortestPathDistance(강남역, 남부터미널역);

		// then
		assertThat(stations.size()).isEqualTo(3);
		assertThat(distance).isEqualTo(13);
	}
}