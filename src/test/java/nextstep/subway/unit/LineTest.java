package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

	/**
	 * Given 노선 하나를 만든다.
	 * When 새로운 섹션을 지하철 노선에 추가하면
	 * Then line의 sections에 추가가 되어 있다.
	 */
	@DisplayName("지하철 노선에 섹션을 추가한다.")
	@Test
	void addSection() {
		//given
		Line 신분당선 = new Line("신분당선", "red");

		//when
		Station 광교역 = new Station("광교역");
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선.addSection(광교역, 광교중앙역, 10);

		//then
		assertAll(
				() -> assertThat(신분당선.getSections().size() == 1),
				() -> assertThat(신분당선.getSections()).contains(new Section(신분당선, 광교역, 광교중앙역, 10))
		);
	}

	@Test
	void getStations() {
	}

	@Test
	void removeSection() {
	}
}
