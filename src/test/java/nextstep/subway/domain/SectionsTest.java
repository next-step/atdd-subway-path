package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

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
		Line line = new Line("4호선", "blue");
		Station upStation = new Station("동대문");
		Station downStation = new Station("동대문역사문화공원");
		int distance = 10;

		sections.addSection(line, upStation, downStation, distance);

		assertThat(sections.getList()).hasSize(1);
	}

}
