package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.ui.SubwayException;

@SpringBootTest
@Transactional
public class PathServiceTest {
	private Long 교대역;
	private Long 강남역;
	private Long 양재역;
	private Long 남부터미널역;
	private Long 신분당선;
	private Long 이호선;
	private Long 삼호선;

	@Autowired
	private LineService lineService;
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private LineRepository lineRepository;
	@Autowired
	private PathService pathService;

	/**
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재역
	 */
	@BeforeEach
	void setUp() {
		교대역 = stationRepository.save(new Station("교대역")).getId();
		강남역 = stationRepository.save(new Station("강남역")).getId();
		양재역 = stationRepository.save(new Station("양재역")).getId();
		남부터미널역 = stationRepository.save(new Station("남부터미널역")).getId();

		신분당선 = lineRepository.save(new Line("신분당선", "red")).getId();
		이호선 = lineRepository.save(new Line("이호선", "green")).getId();
		삼호선 = lineRepository.save(new Line("삼호선", "orange")).getId();

		lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 10));
		lineService.addSection(이호선, new SectionRequest(교대역, 강남역, 10));
		lineService.addSection(삼호선, new SectionRequest(교대역, 남부터미널역, 2));
		lineService.addSection(삼호선, new SectionRequest(남부터미널역, 양재역, 3));
	}

	/**
	 * Given 지하철 노선을 추가하고
	 * When 출발역과 도착역으로 경로를 조회하면
	 * Then 경로에 있는 역 목록과 거리를 응답한다.
	 */
	@DisplayName("경로 조회 성공")
	@Test
	void getPaths_성공() {
		// given 지하철 노선 생성됨

		// when 교대역-양재역 경로 조회
		PathResponse response = pathService.findPath(교대역, 양재역);

		// then 교대역-남부터미널역-양재역
		List<Long> stations = response.getStations().stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(response.getDistance()).isEqualTo(5);
	}

	/**
	 * Given 지하철 노선을 추가하고
	 * When 출발역과 도착역을 동일한 역으로 경로를 조회하면
	 * Then 경로조회에 실패한다.
	 */
	@DisplayName("출발역과 도착역이 같은 경우")
	@Test
	void getPaths_실패1() {
		// given 지하철 노선 생성됨

		// when 교대역-양재역 경로 조회
		// AND then 실패
		assertThatThrownBy(() -> pathService.findPath(교대역, 교대역)).isInstanceOf(SubwayException.class);
	}

	/**
	 * Given 서로 연결되어있지 않는 지하철 노선을 추가하고
	 * When 연결되지 않은 역끼리 경로를 조회하면
	 * Then 경로조회에 실패한다.
	 */
	@DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
	@Test
	void getPaths_실패2() {
		// given 연결되지 않은 지하철 노선 추가 생성
		Long 신도림역 = stationRepository.save(new Station("신도림역")).getId();
		Long 영등포역 = stationRepository.save(new Station("영등포역")).getId();
		Long 일호선 = lineRepository.save(new Line("1호선", "blue")).getId();
		lineService.addSection(일호선, new SectionRequest(신도림역, 영등포역, 10));

		// when 신도림역-교대역 경로 조회
		// AND then 실패
		assertThatThrownBy(() -> pathService.findPath(신도림역, 교대역)).isInstanceOf(SubwayException.class);
	}

	/**
	 * Given 지하철 노선을 추가하고
	 * When 존재하지 않는 역의 경로를 조회하면
	 * Then 경로조회에 실패한다.
	 */
	@DisplayName("존재하지 않는 출발역이나 도착역을 조회 할 경우")
	@Test
	void getPaths_실패3() {
		// given 지하철 노선 생성됨

		// when 존재하지 않는 역으로 경로 조회
		// AND then 실패
		assertThatThrownBy(() -> pathService.findPath(교대역, 999L)).isInstanceOf(SubwayException.class);
	}
}
