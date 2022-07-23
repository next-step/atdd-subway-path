package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@DisplayName("Sections클래스를 검증한다")
@ExtendWith(MockitoExtension.class)
class SectionsTest {

	@Mock
	Section section;

	/**
	 * Given sections에 section을 추가한다.
	 * When sections에서 getStation을 하면
	 * Then sections가 가지고 있는 모든 Station이 반환된다.
	 */
	@DisplayName("getStations 메서드를 검증한다")
	@Test
	void getStations() {
	    //given
		Sections sections = new Sections();
		when(section.getUpStation()).thenReturn(new Station("광교역"));
		when(section.getDownStation()).thenReturn(new Station("광교중앙역"));
		sections.add(section);

	    //when
		List<Station> stations = sections.getStations();

		//then
		assertAll(
				() -> assertThat(stations).hasSize(2),
				() -> assertThat(stations).containsExactly(new Station("광교역"), new Station("광교중앙역"))
		);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * When sections에서 removeSection을 하면
	 * Then 응답값이 비어있게 된다.
	 */
	@DisplayName("removeSection을 검증한다")
	@Test
	void removeSection() {
	    //given
		Sections sections = new Sections();
		when(section.getDownStation()).thenReturn(new Station("광교중앙역"));
		sections.add(section);

	    //when
		sections.removeSection(section.getDownStation());

	    //then
		List<Section> response = sections.getSections();
		assertThat(response).isEmpty();
	}

	/**
	 * Given sections에 section을 추가한다.
	 * When section이 한 개가 있는 sections에서 upstaion으로 삭제를 시도하면
	 * Then IllegalArgumentException이 발생한다.
	 */
	@DisplayName("UpStation으로 섹션을 삭제할 수 없다.")
	@Test
	void removeSectionsFail() {
		//given
		Sections sections = new Sections();
		when(section.getUpStation()).thenReturn(new Station("광교역"));
		when(section.getDownStation()).thenReturn(new Station("광교중앙역"));
		sections.add(section);

		//when
		//then
		assertThatThrownBy(() -> sections.removeSection(section.getUpStation()))
				.isInstanceOf(IllegalArgumentException.class);
	}
}