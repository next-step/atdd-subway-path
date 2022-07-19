package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {
	@Test
	void addSection() {
		//given
		Line line = new Line("분당선", "yellow");
		Section section = new Section(line, new Station("서현역"), new Station("이매역"), 10);

		//when
		line.addSection(section);

		//then
		assertThat(line.getSections()).containsExactly(section);
	}

	@Test
	void getStations() {
		//given
		Line line = new Line("분당선", "yellow");
		Station upStation = new Station("서현역");
		Station downStation = new Station("이매역");
		line.addSection(new Section(line, upStation, downStation, 10));

		//when
		List<Station> stationList = line.getStations();

		//then
		assertThat(stationList).containsExactly(upStation, downStation);
	}

	@Test
	void removeSection() {
		//given
		Line line = new Line("분당선", "yellow");
		Section section = new Section(line, new Station("서현역"), new Station("이매역"), 10);
		line.addSection(section);

		//when
		line.removeSection(section);

		//then
		assertThat(line.getSections()).isEmpty();
	}
}
