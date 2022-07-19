package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

	/**
	 * Given 노선 하나를 만들고, 섹션을 추가한다.
	 * When 노선에서 getStation을 하면
	 * Then 노선의 모든 정보가 반환된다.
	 */
	@DisplayName("지하철 노선의 역 리스트를 반환한다")
	@Test
	void getStations() {
		//given
		Line 신분당선 = new Line("신분당선", "red");
		Station 광교역 = new Station("광교역");
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선.addSection(광교역, 광교중앙역, 10);

		//when
		List<Station> 신분당선_응답 = 신분당선.getStations();

		//then
		assertAll(
				() -> assertThat(신분당선_응답).hasSize(2),
				() -> assertThat(신분당선_응답).containsExactly(new Station("광교역"), new Station("광교중앙역"))
		);
	}

	/**
	 * Given 노선에 섹션 1개가 추가 된다.
	 * When 노선의 downStation을 삭제하면
	 * Then 노선에서 해당 섹션이 삭제된다.
	 */
	@DisplayName("지하철 노선의 섹션을 삭제한다")
	@Test
	void removeSection() {
		//given
		Line 신분당선 = new Line("신분당선", "red");
		Station 광교역 = new Station("광교역");
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선.addSection(광교역, 광교중앙역, 10);

		//when
		신분당선.removeSection(광교중앙역);

		//then
		List<Section> 신분당선_응답 = 신분당선.getSections();
		assertThat(신분당선_응답).isEmpty();
	}
}
