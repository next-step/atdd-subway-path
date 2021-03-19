package nextstep.subway.path.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

@SpringBootTest
@Transactional
public class PathServiceTest {

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private StationRepository stationRepository;

	@Autowired
	private PathService pathService;

	private Station 강남역;
	private Station 역삼역;
	private Station 남부터미널역;
	private Station 양재역;
	private Station 교대역;
	private Line 신분당선;
	private Line 이호선;
	private Line 삼호선;

	@BeforeEach
	void setUp() {
		강남역 = stationRepository.save(new Station("강남역"));
		역삼역 = stationRepository.save(new Station("역삼역"));
		남부터미널역 = stationRepository.save(new Station("남부터미널역"));
		양재역 = stationRepository.save(new Station("양재역"));
		교대역 = stationRepository.save(new Station("교대역"));

		신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 10));
		이호선 = lineRepository.save(new Line("이호선", "green", 교대역, 강남역, 10));
		삼호선 = lineRepository.save(new Line("삼호선", "yellow", 교대역, 남부터미널역, 3));
	}

	@DisplayName("출발역부터 도착역까지의 최단 경로를 조회한다.")
	@Test
	void findShortestPath() {
		// when
		PathResponse pathResponse = pathService.searchPath(강남역.getId(), 남부터미널역.getId());

		// then
		assertThat(pathResponse.getDistance()).isEqualTo(13);
		assertThat(pathResponse.getStations())
			.usingRecursiveFieldByFieldElementComparator()
			.containsExactlyElementsOf(
				Arrays.asList(
					StationResponse.of(강남역),
					StationResponse.of(교대역),
					StationResponse.of(남부터미널역))
			);
	}
}
