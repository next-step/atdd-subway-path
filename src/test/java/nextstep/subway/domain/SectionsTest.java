package nextstep.subway.domain;

import nextstep.subway.exception.CannotInsertLongerSectionException;
import nextstep.subway.exception.CannotInsertSameDistanceSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Sections클래스를 검증한다")
@ExtendWith(MockitoExtension.class)
class SectionsTest {

	@Mock
	Section section;

	@Mock
	Section insertSection;

	/**
	 * Given sections에 section을 추가한다.
	 * Given sections에서 upStation으로 새로운 section을 추가한다
	 * When sections를 조회하면
	 * Then 두 개의 구간이 등록되어 있다.
	 */
	@DisplayName("upStation으로 addSections를 하면 성공한다.")
	@Test
	void addSectionsWithUpStation() {
		//given
		Sections sections = new Sections();
		when(section.getUpStation()).thenReturn(new Station("광교역"));
		when(section.getDownStation()).thenReturn(new Station("상현역"));
		when(section.getDistance()).thenReturn(10);
		sections.add(section);

		when(insertSection.getUpStation()).thenReturn(new Station("광교역"));
		when(insertSection.getDownStation()).thenReturn(new Station("광교중앙역"));
		when(insertSection.getDistance()).thenReturn(3);
		sections.add(insertSection);

		//when
		List<Station> stations = sections.getStations();
		List<Section> sectionsResponse = sections.getSections();

		//then
		assertAll(
				() -> assertThat(stations).hasSize(3),
				() -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(7),
				() -> assertThat(sectionsResponse.get(1).getDistance()).isEqualTo(3)
		);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * When 구간이 길이가 더 큰 section을 삽입하려하면,
	 * Then 실패한다.
	 */
	@DisplayName("구간이 길이가 더 큰 section을 삽입하려하면 실패한다.")
	@Test
	void addSectionsWithUpStationFailOnLongerDistance() {
		//given
		Sections sections = new Sections();
		when(section.getUpStation()).thenReturn(new Station("광교역"));
		when(section.getDownStation()).thenReturn(new Station("상현역"));
		when(section.getDistance()).thenReturn(10);
		sections.add(section);

		when(insertSection.getUpStation()).thenReturn(new Station("광교역"));
		when(insertSection.getDownStation()).thenReturn(new Station("광교중앙역"));
		when(insertSection.getDistance()).thenReturn(15);

		//when
		//then
		assertAll(
				() -> assertThatThrownBy(() -> sections.add(insertSection))
							.isInstanceOf(CannotInsertLongerSectionException.class),
				() -> {
					when(insertSection.getDistance()).thenReturn(10);
					assertThatThrownBy(() -> sections.add(insertSection))
							.isInstanceOf(CannotInsertSameDistanceSectionException.class);
				}
		);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * Given 기존 section의 upStation과 일치하는 downStation을 가진 섹션을 추가한다.
	 * When sections를 조회하면
	 * Then 두 개의 구간이 등록되어 있다.
	 */
	@DisplayName("새로운 구간의 downStation이 기존 구간의 upStation과 일치하는 경우 addSection이 성공한다.")
	@Test
	void addSectionsWithDownStationEqualsUpStation() {
		//given
		Sections sections = new Sections();
		when(section.getUpStation()).thenReturn(new Station("광교역"));
		when(section.getDownStation()).thenReturn(new Station("상현역"));
		when(section.getDistance()).thenReturn(10);
		sections.add(section);

		//given
		when(insertSection.getUpStation()).thenReturn(new Station("신사역"));
		when(insertSection.getDownStation()).thenReturn(new Station("광교역"));
		when(insertSection.getDistance()).thenReturn(3);
		sections.add(insertSection);

		//when
		List<Station> stations = sections.getStations();
		List<Section> sectionsResponse = sections.getSections();
		
		//then
		assertAll(
				() -> assertThat(stations).hasSize(3),
				() -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(3),
				() -> assertThat(sectionsResponse.get(1).getDistance()).isEqualTo(10)
		);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * Given sections에 insertSection1을 추가한다.
	 * Given sections에 insertSection2을 추가한다.
	 * When sections에서 getStation을 하면
	 * Then sections가 가지고 있는 모든 Station이 반환된다.
	 */
	@DisplayName("getStations 메서드를 검증한다")
	@Test
	void getStations() {
	    //given
		Sections sections = new Sections();
		when(section.getUpStation()).thenReturn(new Station("광교역"));
		when(section.getDownStation()).thenReturn(new Station("판교역"));
		sections.add(section);

		//given
		when(insertSection.getUpStation()).thenReturn(new Station("광교역"));
		when(insertSection.getDownStation()).thenReturn(new Station("정자역"));
		sections.add(insertSection);

		//given
		Section insertSection2 = mock(Section.class);
		when(insertSection2.getUpStation()).thenReturn(new Station("광교역"));
		when(insertSection2.getDownStation()).thenReturn(new Station("광교중앙역"));
		sections.add(insertSection2);

	    //when
		List<Station> stations = sections.getStations();
		for (Station station : stations) {
			System.out.println(station.getName());
		}
		//then
		assertAll(
				() -> assertThat(stations).hasSize(4),
				() -> assertThat(stations)
						.containsExactly(new Station("광교역"), new Station("광교중앙역"),
								new Station("정자역"), new Station("판교역"))
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