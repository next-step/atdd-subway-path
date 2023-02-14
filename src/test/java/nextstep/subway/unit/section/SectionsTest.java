package nextstep.subway.unit.section;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

class SectionsTest {

	private Station 역0;
	private Station 역1;
	private Station 역2;
	private Station 역3;

	private Section 구간1;
	private Section 구간2;
	private Section 구간3;

	private Sections sections;

	@BeforeEach
	void setup() {
		역0 = new Station("역0");
		역1 = new Station("역1");
		역2 = new Station("역2");
		역3 = new Station("역3");

		구간1 = new Section(역1, 역2, 10);
		구간2 = new Section(역0, 역1, 5);
		구간3 = new Section(역2, 역3, 5);

		sections = new Sections();
		sections.addSection(구간1);
		sections.addSection(구간2);
		sections.addSection(구간3);
	}

	@Test
	void removeSection() {
	}

	@DisplayName("등록하려는 구간의 역이 이미 다 등록되어 있을 때")
	@Test
	void existAllStationTest() {
		// then
		assertThrows(IllegalArgumentException.class, () -> sections.addSection(구간1));
	}

	@DisplayName("등록하려는 구간의 역이 이미 다 등록되어 있을 때")
	@Test
	void notExistStationTest() {
		// given
		Station 역5 = new Station("역5");
		Station 역6 = new Station("역6");
		Section 구간5 = new Section(역5, 역6, 10);

		// then
		assertThrows(IllegalArgumentException.class, () -> sections.addSection(구간5));
	}

	@DisplayName("등록하려는 구간의 길이가 기존의 길이보다 크거나 같을 때")
	@Test
	void checkDistanceTest() {
		// given
		Station 역2_1 = new Station("역5");
		Section 구간2_1 = new Section(역2, 역2_1, 5);

		// then
		assertThrows(IllegalArgumentException.class, () -> sections.addSection(구간2_1));
	}

	@DisplayName("등록한 구간을 순서대로 조회")
	@Test
	void getOrderStation() {
		// when
		List<Station> orderStations = sections.getOrderStation();

		// then
		assertThat(orderStations).containsExactly(역0, 역1, 역2, 역3);
	}

	@DisplayName("등록한 구간의 총 길이를 조회")
	@Test
	void getDistance() {
		// when
		int totalDistance = sections.getDistance();

		// then
		assertThat(totalDistance).isEqualTo(20);
	}
}
