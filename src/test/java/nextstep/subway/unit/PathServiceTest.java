package nextstep.subway.unit;

import static nextstep.subway.unit.LineStaticValues.*;
import static org.assertj.core.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

@SpringBootTest
@Transactional
public class PathServiceTest {
	@Autowired
	LineRepository lineRepository;
	@Autowired
	StationRepository stationRepository;
	@Autowired
	PathService pathService;

	Station 교대역;
	Station 강남역;
	Station 양재역;
	Station 남부터미널역;

	Line 이호선;
	Line 삼호선;
	Line 신분당선;

	@BeforeEach
	void setUp() {
		교대역 = stationRepository.save(new Station("교대역"));
		강남역 = stationRepository.save(new Station("강남역"));
		양재역 = stationRepository.save(new Station("양재역"));
		남부터미널역 = stationRepository.save(new Station("남부터미널역"));

		이호선 = lineRepository.save(new Line("2호선", "green"));
		삼호선 = lineRepository.save(new Line("3호선", "orange"));
		신분당선 = lineRepository.save(new Line("신분당선", "red"));

		이호선.addSection(교대역, 강남역, DISTANCE_VALUE_10);
		삼호선.addSection(강남역, 양재역, DISTANCE_VALUE_10);
		신분당선.addSection(교대역, 남부터미널역, DISTANCE_VALUE_10);
		삼호선.addSection(남부터미널역, 양재역, DISTANCE_VALUE_3);
	}

	@Test
	@DisplayName("최단거리 조회")
	void getPaths() {
		PathResponse response = pathService.getPath(교대역.getId(), 양재역.getId());

		assertThat(response.getStationList()).hasSize(3)
			.containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(response.getDistance()).isEqualTo(13);
	}

	@Test
	@DisplayName("최단거리 조회-출발,도착역 동일")
	void getPathsBySameStations() {
		assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 교대역.getId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("최단거리 조회-등록되 있지 않은 출발역")
	void getPathsWithNotExistingUpStation() {
		Station 녹번역 = stationRepository.save(new Station("녹번역"));
		assertThatThrownBy(() -> pathService.getPath(녹번역.getId(), 교대역.getId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("최단거리 조회-등록되 있지 않은 도착역")
	void getPathsWithNotExistingDownStation() {
		Station 녹번역 = stationRepository.save(new Station("녹번역"));
		assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 녹번역.getId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("최단거리 조회-연결되지 않은역")
	void getPathsWithoutConnecting() {
		//when
		Station 신도림역 = stationRepository.save(new Station("신도림역"));
		Station 영등포역 = stationRepository.save(new Station("영등포역"));
		Line 일호선 = lineRepository.save(new Line("1호선", "blue"));
		일호선.addSection(신도림역, 영등포역, DISTANCE_VALUE_10);
		//then
		assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 신도림역.getId()))
			.isInstanceOf(BusinessException.class);
	}
}
