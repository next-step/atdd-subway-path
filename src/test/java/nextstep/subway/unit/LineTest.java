package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {
	@Test
	void addSection() throws Exception {
		// given
		Line line = LINE_4();
		int distance = 10;

		// when
		line.addSection(withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), distance);
		List<Section> sections = line.getSections();

		//then
		assertThat(sections).hasSize(2);
	}

	@Test
	void getStations() throws Exception {
		// given
		Line line = LINE_4();

		// when
		List<Station> stations = line.getStations();

		// then
		assertThat(stations).containsExactly(withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID));
	}

	@Test
	void removeSection() {

	}
}
