package subway.unit.path;

import static subway.fixture.station.StationEntityFixture.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import subway.dto.path.PathResponse;
import subway.path.PathService;
import subway.station.Station;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
	@InjectMocks
	private PathService pathService;

	@DisplayName("출발역과 도착역의 최단 거리를 조회한다.")
	@Test
	void findShortestPath() {
		// given
		Station 교대역 = 교대역();
		Station 양재역 = 양재역();
		Long 교대역_id = 교대역.getId();
		Long 양재역_id = 양재역.getId();

		// when
		PathResponse pathResponse = pathService.findShortestPath(교대역_id, 양재역_id);

		// then
		Station 남부터미널역 = 남부터미널역();
		List<Station> shortestPath = List.of(교대역, 남부터미널역, 양재역);
		PathResponse expectedResponse = new PathResponse(shortestPath, 4);
		Assertions.assertThat(pathResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}
}
