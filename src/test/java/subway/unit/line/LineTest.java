package subway.unit.line;

import static org.assertj.core.api.Assertions.*;
import static subway.fixture.line.LineEntityFixture.*;
import static subway.fixture.station.StationEntityFixture.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import subway.fixture.line.LineEntityFixture;
import subway.line.Line;
import subway.line.Section;
import subway.station.Station;

@DisplayName("Line 도메인 테스트")
class LineTest {
	@DisplayName("노선의 이름은 공백이 아니고 20자 이하이다.")
	@NullAndEmptySource
	@ValueSource(strings = {"21자입니다다다다다다다다다다다다다다다다", " "})
	@ParameterizedTest
	void failCreateLineName(String name) {
		String color = "line-color";
		assertThatThrownBy(() -> new Line(name, color))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 이름은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("노선의 색깔은 공백이 아니고 20자 이하이다.")
	@NullAndEmptySource
	@ValueSource(strings = {"21자입니다다다다다다다다다다다다다다다다", " "})
	@ParameterizedTest
	void failCreateLineColor(String color) {
		String name = "line-name";

		assertThatThrownBy(() -> new Line(name, color))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 색깔은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("노선의 이름은 공백 또는 20자 이하로 변경할 수 없다.")
	@NullAndEmptySource
	@ValueSource(strings = {"21자입니다다다다다다다다다다다다다다다다", " "})
	@ParameterizedTest
	void failChangeLineName(String name) {
		Line 신분당선 = 신분당선();
		assertThatThrownBy(() -> 신분당선.changeName(name))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 이름은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("노선의 색깔은 공백 또는 20자 이하로 변경할 수 없다.")
	@NullAndEmptySource
	@ValueSource(strings = {"21자입니다다다다다다다다다다다다다다다다", " "})
	@ParameterizedTest
	void failChangeLineColor(String color) {
		Line 신분당선 = 신분당선();
		assertThatThrownBy(() -> 신분당선.changeColor(color))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("노선의 색깔은 공백이 아니거나 20자 이하여야 합니다.");
	}

	@DisplayName("노선의 이름은 공백이 아닌 20자 이하로 변경할 수 있다.")
	@Test
	void successChangeName() {
		// given
		String changedName = "변경된 노선";
		Line 신분당선 = 신분당선();

		// when
		신분당선.changeName(changedName);

		// then
		assertThat(changedName).isEqualTo(신분당선.getName());
	}

	@DisplayName("노선의 색깔은 공백이 아닌 20자 이하로 변경할 수 있다.")
	@Test
	void successChangeColor() {
		// given
		String changedColor = "변경된 색깔";
		Line 신분당선 = 신분당선();

		// when
		신분당선.changeColor(changedColor);

		// then
		assertThat(changedColor).isEqualTo(신분당선.getColor());
	}

	@DisplayName("노선에 구간을 추가할 수 있다.")
	@Test
	void successAddSection() {
		// given
		Line 신분당선 = 신분당선();
		Station 강남역 = 강남역();
		Station 양재역 = 양재역();
		Section newSection = new Section(신분당선, 강남역, 양재역, DISTANCE);

		// when
		신분당선.addSection(newSection);

		// then
		assertThat(신분당선).usingRecursiveComparison().isEqualTo(LineEntityFixture.신분당선_1구간_추가());
	}

	@DisplayName("노선에 이미 존재하는 정류장은 다시 추가할 수 없다.")
	@Test
	void failAddSectionAlreadyExistsStation() {
		// given
		Line 신분당선_2구간_추가 = 신분당선_2구간_추가();
		Station 양재역 = 양재역();
		Station 논현역 = 논현역();
		Section newSection = new Section(신분당선_2구간_추가, 양재역, 논현역, DISTANCE);

		// when
		// then
		assertThatThrownBy(() -> 신분당선_2구간_추가.addSection(newSection))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("추가 할려는 정류장은 이미 해당 노선에 존재하는 정류장입니다.");
	}

	@DisplayName("노선의 마지막 정류장을 삭제할 수 있다.")
	@Test
	void successRemoveSection() {
		// given
		Line 신분당선_2구간_추가 = 신분당선_2구간_추가();
		Station 논현역 = 논현역();

		// when
		신분당선_2구간_추가.removeFinalStation(논현역);

		// then
		assertThat(신분당선_2구간_추가.getSections().getSortedStations()).hasSize(2);
	}

	@DisplayName("노선에 두개의 정류장만 존재하는 경우, 정류장을 삭제할 수 없다.")
	@Test
	void failRemoveSection() {
		// given
		Line 신분당선_1구간_추가 = 신분당선_1구간_추가();
		Station 양재역 = 양재역();

		// when
		// then
		assertThatThrownBy(() -> 신분당선_1구간_추가.removeFinalStation(양재역))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 노선은 두개의 정류장만 존재 하므로, 삭제할 수 없습니다.");
	}

	@DisplayName("노선 구간을 정렬된 상태로 가져온다.")
	@Test
	void successSortedSection() {
		// given
		Line 신분당선 = 신분당선();
		Station 강남역 = 강남역();
		Station 양재역 = 양재역();
		Station 논현역 = 논현역();
		신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
		신분당선.addSection(new Section(신분당선, 강남역, 논현역, 8));

		// when
		// then
		List<Section> expectedSections =
			List.of(
				new Section(신분당선, 강남역, 논현역, 8),
				new Section(신분당선, 논현역, 양재역, 2)
			);
		Assertions.assertThat(신분당선.getSortedSections()).isEqualTo(expectedSections);
	}
}
