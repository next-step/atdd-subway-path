package nextstep.subway.unit;

import static nextstep.subway.unit.LineStaticValues.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;

public class PathFinderTest {
	Station 교대역;
	Station 강남역;
	Station 양재역;
	Station 남부터미널역;
	List<Line> lines;
	@Autowired
	LineService lineService;

	@BeforeEach
	void setUp() {
		교대역 = new Station(1L, "교대역");
		강남역 = new Station(2L, "강남역");
		양재역 = new Station(3L, "양재역");
		남부터미널역 = new Station(4L, "남부터미널역");

		Line 이호선 = new Line("2호선", "green");
		Line 삼호선 = new Line("3호선", "orange");
		Line 신분당선 = new Line("신분당선", "red");

		이호선.addSection(교대역, 강남역, DISTANCE_VALUE_10);
		삼호선.addSection(교대역, 남부터미널역, DISTANCE_VALUE_10);
		신분당선.addSection(교대역, 남부터미널역, DISTANCE_VALUE_10);
		삼호선.addSection(남부터미널역, 양재역, DISTANCE_VALUE_3);

		lines = Arrays.asList(이호선, 삼호선, 신분당선);
	}

	@Test
	@DisplayName("최단경로 조회")
	void getShortestPaths() {

		/**
		 * 교대역    --- *2호선* ---   강남역
		 * |                        |
		 * *3호선*                   *신분당선*
		 * |                        |
		 * 남부터미널역  --- *3호선* ---   양재
		 */

		//when
		List<String> stations = PathFinder.getShorPath(lineService, 교대역.getId(), 양재역.getId());
		List<Station> stationList = getAllStations().stream()
			.filter(station -> stations.contains(station.getId().toString()))
			.collect(Collectors.toList());
		//then
		assertThat(stationList).hasSize(3)
			.containsExactly(교대역, 남부터미널역, 양재역);
	}

	private List<Station> getAllStations() {
		return lines.stream()
			.flatMap(line -> line.getStations().stream())
			.distinct()
			.collect(Collectors.toList());
	}

}
