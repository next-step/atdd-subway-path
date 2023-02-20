package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

class LineTest {
	@DisplayName("구간 추가 - 새로운 역이 상행 종점역과 하행 종점역 사이로 하는 구간 생성")
	@Test
	void addSectionBetweenUpStationAndDwonStation() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 역삼역 = new Station("역삼역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(강남역, 역삼역, 9);

		// Then
		assertThat(이호선.getStations()).containsOnly(강남역, 선릉역, 역삼역);
	}

	@DisplayName("구간 추가 - 새로운 역이 상행 종점역으로 하는 구간 생성")
	@Test
	void addSectionToUpStation() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 교대역 = new Station("교대역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(교대역, 강남역, 10);
		// Then
		assertThat(이호선.getStations()).containsOnly(강남역, 선릉역, 교대역);
	}

	@DisplayName("구간 추가 - 새로운 역이 하행 종점역으로 하는 구간 생성")
	@Test
	void addSectionToDownStation() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 삼성역 = new Station("삼성역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(선릉역, 삼성역, 10);
		// Then
		assertThat(이호선.getStations()).containsOnly(강남역, 선릉역, 삼성역);
	}

	@DisplayName("구간 추가 - 노선이 생성 되는 경우")
	@Test
	void addSection() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		// Then
		assertThat(이호선.getSections().getSections()).isNotEmpty();
	}

	@DisplayName("노선의 역 조회")
	@Test
	void getStations() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);

		// Then
		assertThat(이호선.getStations()).containsOnly(강남역, 선릉역);
	}

	@DisplayName("구간 삭제")
	@Test
	void removeSection() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 삼성역 = new Station("삼성역");

		Line 이호선 = new Line("2호선", "green");
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(선릉역, 삼성역, 20);

		// When
		이호선.removeSection(삼성역);

		// Then
		assertThat(이호선.getStations()).containsOnly(강남역, 선릉역);
	}
}
