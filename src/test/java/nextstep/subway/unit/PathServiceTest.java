package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("PathService를 검증한다.")
@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

	@Mock
	StationRepository stationRepository;
	@Mock
	LineRepository lineRepository;
	@Mock
	StationService stationService;

	@InjectMocks
	PathService pathService;

	private Line 이호선;
	private Line 팔호선;

	private Station 종합운동장역;
	private Station 석촌역;
	private Station 잠실역;

	@BeforeEach
	void setUp() {
		종합운동장역 = new Station("종합운동장역");
		잠실역 = new Station("잠실역");
		석촌역 = new Station("석촌역");

		이호선 = new Line("2호선", "green");
		이호선.addSection(종합운동장역, 잠실역, 10);

		팔호선 = new Line("8호선", "pink");
		팔호선.addSection(종합운동장역, 석촌역, 5);
		팔호선.addSection(석촌역, 잠실역, 3);
	}

	/**
	 * Given 지하철 노선을 등록한다
	 * When 시작역과 종점역이 주어지면
	 * Then 경로에 포함된 역 목록과 거리가 반환된다.
	 */
	@DisplayName("findPath를 검증한다")
	@Test
	void findPath() {
		//given
		/**
		 * 종합운동장 -2호선(10)- 잠실
		 *    \  			|
		 *	8호선(5)	8호선(3)
		 *    	\		   |
		 *        석촌
		 */
		when(stationRepository.findById(1L)).thenReturn(Optional.of(종합운동장역));
		when(stationRepository.findById(4L)).thenReturn(Optional.of(잠실역));
		when(stationService.createStationResponse(종합운동장역)).thenReturn(stationResponse를_반환한다(종합운동장역));
		when(stationService.createStationResponse(잠실역)).thenReturn(stationResponse를_반환한다(잠실역));
		when(stationService.createStationResponse(석촌역)).thenReturn(stationResponse를_반환한다(석촌역));
		when(lineRepository.findAll()).thenReturn(List.of(이호선, 팔호선));

		//when
		PathResponse response = pathService.findPath(1L, 4L);

		//then
		List<StationResponse> stationResponses = response.getStations();
		assertAll(
				() -> assertThat(stationResponses).hasSize(3),
				() -> {
					List<String> names = stationResponses.stream()
							.map(StationResponse::getName)
							.collect(Collectors.toList());
					assertThat(names).containsExactly(종합운동장역.getName(), 석촌역.getName(), 잠실역.getName());
				},
				() -> assertThat(response.getDistance()).isEqualTo(8)
		);
	}

	private StationResponse stationResponse를_반환한다(Station station) {
		return new StationResponse(station.getId(), station.getName());
	}
}
