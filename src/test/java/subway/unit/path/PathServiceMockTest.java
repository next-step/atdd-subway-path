package subway.unit.path;

import static org.mockito.BDDMockito.*;
import static subway.fixture.line.LineEntityFixture.*;
import static subway.fixture.station.StationEntityFixture.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import subway.dto.path.PathResponse;
import subway.line.Line;
import subway.line.LineRepository;
import subway.line.Section;
import subway.path.PathService;
import subway.station.Station;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
	@InjectMocks
	private PathService pathService;

	@Mock
	private LineRepository lineRepository;

	private Station 교대역;
	private Station 강남역;
	private Station 남부터미널역;
	private Station 양재역;

	private Line 이호선;
	private Line 삼호선;
	private Line 신분당선;

	@BeforeEach
	void setUp() {
		교대역 = 교대역();
		강남역 = 강남역();
		남부터미널역 = 남부터미널역();
		양재역 = 양재역();

		이호선 = 이호선();
		이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

		삼호선 = 삼호선();
		삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
		삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 2));

		신분당선 = 신분당선();
		신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
	}

	@DisplayName("출발역과 도착역의 최단 거리를 조회한다.")
	@Test
	void successFindShortestPath() {
		// given
		given(lineRepository.findAll()).willReturn(List.of(이호선, 삼호선, 신분당선));

		// when
		PathResponse pathResponse = pathService.findShortestPath(교대역, 양재역);

		// then
		List<Station> shortestPath = List.of(교대역, 남부터미널역, 양재역);
		PathResponse expectedResponse = new PathResponse(shortestPath, 4);
		Assertions.assertThat(pathResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
	}
}
