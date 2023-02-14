package nextstep.subway.unit;

import static nextstep.subway.common.PathFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.IntegrationUnitTest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.exception.PathErrorCode;
import nextstep.subway.domain.exception.PathSearchException;

class PathServiceTest extends IntegrationUnitTest {

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private StationRepository stationRepository;

	@Autowired
	private LineService lineService;

	@Autowired
	private PathService pathService;

	private Station 교대역;
	private Station 강남역;
	private Station 양재역;
	private Station 남부터미널역;

	private Long 삼호선;

	/**
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재
	 */
	@BeforeEach
	void setUp() {
		교대역 = stationRepository.save(교대역());
		강남역 = stationRepository.save(강남역());
		양재역 = stationRepository.save(양재역());
		남부터미널역 = stationRepository.save(남부터미널역());

		lineRepository.save(new Line("2호선", "green", 교대역, 강남역, 10));
		lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 10));
		삼호선 = lineRepository.save(new Line("3호선", "orange", 교대역, 남부터미널역, 2)).getId();

	}

	@DisplayName("교대역에서 양재역까지 최단 경로와 최단 거리를 얻어낸다")
	@Test
	void 교대역에서_양재역까지_최단_경로와_최단_거리를_얻어낸다() {
		// given
		SectionRequest sectionRequest = new SectionRequest(남부터미널역.getId(), 양재역.getId(), 3);
		lineService.addSection(삼호선, sectionRequest);

		// when
		PathResponse pathResponse = pathService.getPath(교대역.getId(), 양재역.getId());

		// then
		assertAll(
			() -> assertThat(pathResponse.getStations()).hasSize(3),
			() -> assertThat(pathResponse.getDistance()).isEqualTo(5)
		);
	}

	@DisplayName("출발역과 도착역이 같은경우 예외가 발생한다")
	@Test
	void 출발역과_도착역이_같은경우_예외가_발생한다() {
		assertThatThrownBy(() -> pathService.getPath(양재역.getId(), 양재역.getId()))
			.isInstanceOf(PathSearchException.class)
			.hasMessage(PathErrorCode.EQUAL_SEARCH_STATION.getMessage());
	}

	@DisplayName("출발역과 도착역이 연결이되어있지 않은경우 예외가 발생한다")
	@Test
	void 출발역과_도착역이_연결이되어있지_않은경우_예외가_발생한다() {
		Station 동대문역 = stationRepository.save(동대문역());
		Station 혜화역 = stationRepository.save(혜화역());
		lineRepository.save(new Line("4호선", "blue", 동대문역, 혜화역, 6));

		assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 혜화역.getId()))
			.isInstanceOf(PathSearchException.class)
			.hasMessage(PathErrorCode.NOT_CONNECTION.getMessage());
	}

	@DisplayName("최단경로 검색시 존재하지않은 출발역을 요청한 경우 예외가 발생한다")
	@Test
	void 최단경로_검색시_존재하지않은_출발역을_요청한_경우_예외가_발생한다() {
		assertThatThrownBy(() -> pathService.getPath(존재하지않은역, 교대역.getId()))
			.isInstanceOf(PathSearchException.class)
			.hasMessage(PathErrorCode.NOT_FOUND_STATION.getMessage());
	}

	@DisplayName("최단경로 검색시 존재하지않은 도착역을 요청한 경우 예외가 발생한다")
	@Test
	void 최단경로_검색시_존재하지않은_도착역을_요청한_경우_예외가_발생한다() {
		assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 존재하지않은역))
			.isInstanceOf(PathSearchException.class)
			.hasMessage(PathErrorCode.NOT_FOUND_STATION.getMessage());
	}

}
