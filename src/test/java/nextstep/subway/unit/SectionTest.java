package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 Unit Test")
class SectionTest {

	/**
	 * given 역 A - 역 B 구간이 있을 때
	 * when 새로운 역 A - 역 C 구간이 추가되면
	 * then 기존 구간은 역 C - 역 B 로 변경된다.
	 */
	@Test
	void updateUpStation() {
		// given
		Line line = new Line("노선", "red");
		Station stationA = new Station("역A");
		Station stationB = new Station("역B");
		Section section = new Section(line, stationA, stationB, 10);

		// when
		Station stationC = new Station("역C");
		Section newSection = new Section(line, stationA, stationC, 6);

		section.updateUpStationToDownStationOf(newSection);
		// then
		assertThat(section).isEqualTo(new Section(line, stationC, stationB, 4));
	}
}