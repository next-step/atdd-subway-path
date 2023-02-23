package nextstep.subway.unit.path;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;

class PathFinderTest {

	private Station 교대역;
	private Station 강남역;
	private Station 양재역;
	private Station 남부터미널역;
	private Station 사당역;

	private Section 교대_강남;
	private Section 강남_양재;
	private Section 교대_남부터미널;
	private Section 남부터미널_양재;

	private List<Station> stations = new ArrayList<>();
	private List<Section> sections = new ArrayList<>();

	private PathFinder pathFinder;

	@BeforeEach
	void setup() {
		교대역 = new Station(1L, "교대역");
		강남역 = new Station(2L, "강남역");
		양재역 = new Station(3L, "양재역");
		남부터미널역 = new Station(4L, "남부터미널역");
		사당역 = new Station(5L, "사당역");

		교대_강남 = new Section(교대역, 강남역, 10);
		강남_양재 = new Section(강남역, 양재역, 10);
		교대_남부터미널 = new Section(교대역, 남부터미널역, 2);
		남부터미널_양재 = new Section(남부터미널역, 양재역, 3);

		stations = List.of(교대역, 강남역, 양재역, 남부터미널역, 사당역);
		sections = List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재);

		pathFinder = new PathFinder(stations, sections);
	}

	@DisplayName("성공적인 경로 찾기")
	@Test
	void successPathSearch() {
		PathResponse pathResponse = pathFinder.pathSearch(교대역, 양재역);
		assertThat(pathResponse.getDistance()).isEqualTo(5);
		assertThat(pathResponse.getStations()).extracting(StationResponse::getId).containsExactly(1L, 4L, 3L);
	}

	@DisplayName("출발역과 도착역이 같을 때")
	@Test
	void noDifferentStation() {
		assertThrows(IllegalArgumentException.class, () -> pathFinder.pathSearch(교대역, 교대역));
	}

	@DisplayName("출발역과 도착역이 이어져있지 않을 때")
	@Test
	void noConnectStations() {
		assertThrows(IllegalArgumentException.class, () -> pathFinder.pathSearch(교대역, 사당역));
	}
}
