package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {
	private Line 신분당선;
	private Station 강남역;
	private Station 양재역;
	private Section section;

	@BeforeEach
	void setUp() {
		신분당선 = new Line("신분당선", "red");

		강남역 = new Station("강남역");
		양재역 = new Station("양재역");

		section = new Section(신분당선, 강남역, 양재역, 10);

		신분당선.addSection(section);
	}

    @DisplayName("구간 추가")
	@Test
	void addSection() {
		// add section -> setUp()
		assertThat(신분당선.getSections()).contains(section);
	}

    @DisplayName("라인에 해당하는 지하철역 조회")
	@Test
	void getStations() {
		List<Station> stations = 신분당선.getStations();

		System.out.println(stations.toString());
		assertThat(stations).hasSize(2);
		assertThat(stations).containsExactly(강남역, 양재역);
	}

    @DisplayName("구간 제거")
	@Test
	void removeSection() {
		Station 양재시민의숲역 = new Station("양재시민의숲역");
		Section section2 = new Section(신분당선, 양재역, 양재시민의숲역, 20);
		신분당선.addSection(section2);

		신분당선.removeSection(양재시민의숲역);
		List<Station> stations = 신분당선.getStations();

		assertThat(stations).containsExactly(강남역, 양재역);
	}

	@DisplayName("구간이 하나만 남은 경우 구간을 삭제할 수 없다.")
	@Test
	void removeSectionError() {
		assertThatThrownBy(() -> 신분당선.removeSection(양재역)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("하행 종점역이 아닌경우 구간을 삭제할 수 없다.")
	@Test
	void removeSectionError2() {
		assertThatThrownBy(() -> 신분당선.removeSection(강남역)).isInstanceOf(IllegalArgumentException.class);
	}
}
