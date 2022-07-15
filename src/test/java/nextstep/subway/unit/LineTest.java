package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

class LineTest {

	@Test
	void addSection() {
		//given
		Line line = new Line(1L, "신분당선", "color");

		//when
		line.addSection(new Station(1L, "강남역"), new Station(2L, "역삼역"), 1);

		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@Test
	void getStations() {
		//given
		//when
		Line line = new Line(1L, "신분당선", "color");
		line.addSection(new Station(1L, "강남역"), new Station(2L, "역삼역"), 1);
		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@Test
	void removeSection() {
		//given
		Line line = new Line(1L, "신분당선", "color");
		line.addSection(new Station(1L, "강남역"), new Station(2L, "역삼역"), 1);
		//when
		line.removeSection(new Station(2L, "역삼역"));
		//then
		assertThat(line.getSections()).hasSize(0);
	}
}
