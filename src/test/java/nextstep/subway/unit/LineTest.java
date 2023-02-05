package nextstep.subway.unit;

import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.common.LineFixtures;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

class LineTest {
	@Test
	void addSection() {
		// given
		Line line = LineFixtures.LINE_4;
		int distance = 10;

		// when
		line.addSection(동대문, 동대문역사문화공원, distance);
		List<Section> sections = line.getSections();

		//then
		assertThat(sections).hasSize(1);
	}

	@Test
	void getStations() {
	}

	@Test
	void removeSection() {
	}
}
