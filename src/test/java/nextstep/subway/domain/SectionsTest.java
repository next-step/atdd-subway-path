package nextstep.subway.domain;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.StationFixtures;

class SectionsTest {

	private Sections sections;

	@BeforeEach
	void setUp() {
		sections = new Sections();
	}

	@DisplayName("구간 추가에 성공한다")
	@Test
	void addSection() throws Exception {
		// given
		Line line = LINE_4();
		int distance = 10;

		// when
		sections.addSection(line, 동대문역사문화공원, 충무로, distance);

		// then
		assertThat(sections.getList()).hasSize(1);
	}

	@DisplayName("전체 역 조회에 성공한다")
	@Test
	void getStations() throws Exception {
		// given
		Line line = LINE_4();
		sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), 5);

		// when
		List<Station> stations = sections.getStations();

		// then
		assertThat(stations)
			.containsExactly(
				동대문,
				동대문역사문화공원,
				충무로
			);
	}

	@DisplayName("노선 생성 시 초기 구간을 생성한다")
	@Test
	void createInitializeSection() throws Exception {
		// given
		String lineName = "4호선";
		String lineColor = "blue";

		Station 동대문 = withId(StationFixtures.동대문, 동대문_ID);
		Station 동대문역사문화공원 = withId(StationFixtures.동대문역사문화공원, 동대문역사문화공원_ID);

		// when
		Line line = new Line(
			lineName,
			lineColor,
			동대문,
			동대문역사문화공원,
			10
		);

		// then
		assertAll(
			() -> assertThat(line.getSections()).hasSize(1),
			() -> assertThat(line.getUpStationId()).isEqualTo(동대문.getId()),
			() -> assertThat(line.getDownStationId()).isEqualTo(동대문역사문화공원.getId())
		);

	}
}
