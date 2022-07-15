package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

class LineTest {

	@DisplayName("지하철 구간 추가")
	@Test
	void addSection() {
		//given
		Line line = new Line(1L, "신분당선", "color");

		//when
		line.addSection(new Station(1L, "강남역"), new Station(2L, "역삼역"), 1);

		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("지하철 구간 조회")
	@Test
	void getStations() {
		//given
		//when
		Line line = new Line(1L, "신분당선", "color");
		line.addSection(new Station(1L, "강남역"), new Station(2L, "역삼역"), 1);
		line.addSection(new Station(2L, "역삼역"), new Station(3L, "신도림"), 1);
		//then
		assertThat(line.getStations()).hasSize(3);
	}

	@DisplayName("지하철 구간 삭제")
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

	@DisplayName("지하철 구간 삭제 실패")
	@Test
	void removeSectionFail1() {
		//given
		Line line = new Line(1L, "신분당선", "color");

		//when
		line.addSection(new Station(1L, "강남역"), new Station(2L, "역삼역"), 1);
		line.addSection(new Station(2L, "역삼역"), new Station(3L, "신도림"), 1);

		//then
		assertThrows(IllegalArgumentException.class,
			() -> line.removeSection(new Station(2L, "역삼역")));
	}
}
