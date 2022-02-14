package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.applicaion.exception.NotExistStationException;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
public class PathServiceTest {
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private PathService pathService;


	private Station 홍대입구역;
	private Station 합정역;
	private Station 당산역;
	private Station 김포공항역;
	private Line 이호선;
	private Line 구호선;
	private Line 공항철도선;

	/**
	 * 합정역    --- *2호선* ---   홍대입구역
	 * |             10            |
	 * *2호선* 20              *공항철도선* 20
	 * |            20            |
	 * 당산역  --- *9호선* ---   김포공항역
	 */
	@BeforeEach
	public void setUp() {
		홍대입구역 = stationRepository.save(Station.of("홍대입구역"));
		합정역 = stationRepository.save(Station.of("합정역"));
		당산역 = stationRepository.save(Station.of("당산역"));
		김포공항역 = stationRepository.save(Station.of("김포공항역"));

		이호선 = lineRepository.save(Line.of("이호선", "green", 홍대입구역, 합정역, Distance.from(10)));
		이호선.addSection(합정역, 당산역, Distance.from(20));

		구호선 = lineRepository.save(Line.of("구호선", "gold", 당산역, 김포공항역, Distance.from(20)));

		공항철도선 = lineRepository.save(Line.of("공항철도선", "green", 홍대입구역, 김포공항역, Distance.from(20)));
	}

	@DisplayName("출발지와 도착지의 최단거리 도출에 성공한다.")
	@Test
	void findRoute() {
		//when
		PathResponse pathResponse = pathService.findRoute(합정역.getId(), 김포공항역.getId());

		//then
		assertThat(pathResponse.getStations())
				.extracting(StationResponse::getId)
				.containsExactly(합정역.getId(), 홍대입구역.getId(), 김포공항역.getId());
		assertThat(pathResponse.getDistance())
				.isEqualTo(30);
	}

	@DisplayName("출발지와 도착지가 같을 경우 최단거리 도출에 실패한다.")
	@Test
	void findSameStationRoute() {
		//when & then
		assertThatThrownBy(() -> pathService.findRoute(합정역.getId(), 합정역.getId()))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("출발지나 도착지가 존재하지 않을 경우 최단거리 도출에 실패한다.")
	@Test
	void findNotExistStationRoute() {
		//when & then
		assertThatThrownBy(() -> pathService.findRoute(합정역.getId(), 99L))
				.isInstanceOf(NotExistStationException.class);
	}
}
