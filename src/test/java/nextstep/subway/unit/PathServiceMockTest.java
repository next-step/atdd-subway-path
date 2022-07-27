package nextstep.subway.unit;

import static nextstep.subway.unit.LineStaticValues.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
	@Mock
	LineRepository lineRepository;
	@Mock
	StationRepository stationRepository;

	@Mock
	PathService pathService;

	@Mock
	LineService lineService;

	Station 교대역;
	Station 강남역;
	Station 양재역;
	Station 남부터미널역;

	Line 이호선;
	Line 삼호선;
	Line 신분당선;
	List<LineResponse> lineResponses;

	@BeforeEach
	void setUp() {
		교대역 = new Station(1L, "교대역");
		강남역 = new Station(2L, "강남역");
		양재역 = new Station(3L, "양재역");
		남부터미널역 = new Station(4L, "남부터미널역");

		이호선 = new Line("2호선", "green");
		삼호선 = new Line("3호선", "orange");
		신분당선 = new Line("신분당선", "red");

		이호선.addSection(교대역, 강남역, DISTANCE_VALUE_10);
		삼호선.addSection(강남역, 양재역, DISTANCE_VALUE_10);
		신분당선.addSection(교대역, 남부터미널역, DISTANCE_VALUE_10);
		삼호선.addSection(남부터미널역, 양재역, DISTANCE_VALUE_3);

		lineResponses = Arrays.asList(이호선, 삼호선, 신분당선)
			.stream()
			.map(line -> LineResponse.of(line, line.getStations()
				.stream()
				.map(StationResponse::of)
				.collect(Collectors.toList())))
			.collect(Collectors.toList());
	}

	@Test
	void getPaths() {

		//given
		when(lineService.showLines()).thenReturn(lineResponses);

		//when
		List<LineResponse> lineResponseList = lineService.showLines();
		PathRequest pathRequest = new PathRequest(lineResponseList, 교대역.getId(), 양재역.getId());
		PathResponse pathResponse = pathService.getPath(pathRequest);

		//then
		assertThat(pathResponse.getStationList()).hasSize(3)
			.containsExactly(교대역, 남부터미널역, 양재역);

	}
}
