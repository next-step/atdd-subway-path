package nextstep.subway.domain;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

	private Sections sections;

	@BeforeEach
	void setUp() {
		sections = new Sections();
	}

	@DisplayName("구간 추가에 성공한다")
	@Test
	void addSection() {
		// given
		Line line = LINE_4;
		int distance = 10;

		// when
		sections.addSection(line, 동대문, 동대문역사문화공원, distance);

		// then
		assertThat(sections.getList()).hasSize(1);
	}

	@DisplayName("전체 역 조회에 성공한다")
	@Test
	void getStations() throws Exception {
		// given
		Line line = LINE_4;
		sections.addSection(line, withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 10);
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
}
