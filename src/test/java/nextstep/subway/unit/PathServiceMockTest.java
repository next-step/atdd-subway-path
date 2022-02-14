package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
	@Mock
	private StationService stationService;
	@Mock
	private LineRepository lineRepository;

	private Station 홍대입구역= Station.of("홍대입구역");
	private Station 합정역 = Station.of("합정역");
	private Station 당산역= Station.of("당산역");
	private Station 김포공항역 = Station.of("김포공항역");
	private Line 이호선 = Line.of("이호선", "green", 홍대입구역, 합정역, Distance.from(10));
	private Line 구호선 = Line.of("구호선", "gold", 당산역, 김포공항역, Distance.from(20));
	private Line 공항철도선 = Line.of("공항철도선", "green", 홍대입구역, 김포공항역, Distance.from(20));

	/**
	 * 합정역    --- *2호선* ---   홍대입구역
	 * |             10            |
	 * *2호선* 20              *공항철도선* 20
	 * |            20            |
	 * 당산역  --- *9호선* ---   김포공항역
	 */
	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(홍대입구역, "id", 1L);
		ReflectionTestUtils.setField(합정역, "id", 2L);
		ReflectionTestUtils.setField(당산역, "id", 3L);
		ReflectionTestUtils.setField(김포공항역, "id", 4L);

		ReflectionTestUtils.setField(이호선, "id", 1L);

		이호선.addSection(합정역, 당산역, Distance.from(20));
		ReflectionTestUtils.setField(이호선.getSections().get(0), "id", 1L);
		ReflectionTestUtils.setField(이호선.getSections().get(1), "id", 2L);

		ReflectionTestUtils.setField(구호선, "id", 2L);
		ReflectionTestUtils.setField(구호선.getSections().get(0), "id", 1L);

		ReflectionTestUtils.setField(공항철도선, "id", 3L);
		ReflectionTestUtils.setField(공항철도선.getSections().get(0), "id", 1L);

		when(stationService.findById(2L)).thenReturn(합정역);
		when(stationService.findById(4L)).thenReturn(김포공항역);
		when(lineRepository.findAll()).thenReturn(List.of(이호선, 구호선, 공항철도선));
	}

	@DisplayName("출발지와 도착지의 최단거리 도출")
	@Test
	void findRoute() {
		PathService pathService = new PathService(lineRepository, stationService);
		PathResponse pathResponse = pathService.findRoute(합정역.getId(), 김포공항역.getId());

		assertThat(pathResponse.getStations())
				.extracting(StationResponse::getId)
				.containsExactly(합정역.getId(), 홍대입구역.getId(), 김포공항역.getId());
		assertThat(pathResponse.getDistance())
				.isEqualTo(30);
	}
}
