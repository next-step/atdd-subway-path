package nextstep.subway.domain;

import nextstep.subway.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Sections클래스를 검증한다")
@ExtendWith(MockitoExtension.class)
class SectionsTest {

	private static final int 광교역_판교역_거리 = 10;

	private Sections sections;

	private Station 광교역;
	private Station 판교역;

	private Line 신분당선;

	@Mock
	Section insertSection;

	@BeforeEach
	void setUp() {
		광교역 = new Station("광교역");
		판교역 = new Station("판교역");
		신분당선 = new Line("신분당선", "red");

		sections = new Sections();
		신분당선에_구간을_추가한다(광교역, 판교역, 광교역_판교역_거리);
	}

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
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선에_구간을_추가한다(광교역, 광교중앙역, 3);

		//when
		List<Station> stations = sections.getStations();
		List<Section> sectionsResponse = sections.getSections();

		//then
		assertAll(
				() -> assertThat(stations).hasSize(3),
				() -> assertThat(stations).containsExactly(광교역, 광교중앙역, 판교역),
				() -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(3),
				() -> assertThat(sectionsResponse.get(1).getDistance()).isEqualTo(7)
		);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * Given sections에서 downStation으로 새로운 section을 추가한다
	 * When sections를 조회하면
	 * Then 두 개의 구간이 등록되어 있다.
	 */
	@DisplayName("downStation으로 addSections를 하면 성공한다.")
	@Test
	void addSectionsWithDownStation() {
		//given
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선에_구간을_추가한다(광교중앙역, 판교역, 3);

		//when
		List<Station> stations = sections.getStations();
		List<Section> sectionsResponse = sections.getSections();

		//then
		assertAll(
				() -> assertThat(stations).hasSize(3),
				() -> assertThat(stations).containsExactly(광교역, 광교중앙역, 판교역),
				() -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(7),
				() -> assertThat(sectionsResponse.get(1).getDistance()).isEqualTo(3)
		);
	}

	/**
	 * Given 새로운 역을 만든다
	 * When 구간이 길이가 더 큰 section을 삽입하려하면,
	 * Then 실패한다.
	 */
	@DisplayName("구간이 길이가 더 큰 section을 삽입하려하면 실패한다.")
	@Test
	void addSectionsWithUpStationFailOnLongerDistance() {
		//given
		Station 광교중앙역 = new Station("광교중앙역");

		//when
		//then
		assertThatThrownBy(() -> 신분당선에_구간을_추가한다(광교역, 광교중앙역, 15))
				.isInstanceOf(CannotInsertLongerSectionException.class);
		assertThatThrownBy(() -> 신분당선에_구간을_추가한다(광교중앙역, 판교역, 15))
				.isInstanceOf(CannotInsertLongerSectionException.class);
	}

	/**
	 * Given 새로운 역을 만든다
	 * When 구간이 길이가 같은 section을 삽입하려하면,
	 * Then 실패한다.
	 */
	@DisplayName("구간이 길이가 같은 section을 삽입하려하면 실패한다.")
	@Test
	void addSectionsWithUpStationFailOnSameDistance() {
		//given
		Station 광교중앙역 = new Station("광교중앙역");
		//when
		//then
		assertThatThrownBy(() -> 신분당선에_구간을_추가한다(광교역, 광교중앙역, 광교역_판교역_거리))
				.isInstanceOf(CannotInsertSameDistanceSectionException.class);
		assertThatThrownBy(() -> 신분당선에_구간을_추가한다(광교중앙역, 판교역, 광교역_판교역_거리))
				.isInstanceOf(CannotInsertSameDistanceSectionException.class);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * When 기존에 등록된 역을 상행역과 하행역으로 가지고 있는 section을 등록하려하면,
	 * Then 실패한다.
	 */
	@DisplayName("새로운 구간 등록 시, 상행역과 하행역이 구간으로 이미 등록되어 있으면 실패한다.")
	@Test
	void addLineSectionFailWithAlreadyAdded() {
		//given
		Station 정자역 = new Station("정자역");
		신분당선에_구간을_추가한다(판교역, 정자역, 5);

		//when
		//then
		assertThatThrownBy(() -> 신분당선에_구간을_추가한다(광교역, 정자역, 20))
				.isInstanceOf(AlreadyRegisteredException.class);
	}

	/**
	 * Given sections에 section을 1개 추가한다.
	 * When 기존 구간에 등록되어 있지 않은 상행역과 하행역을 가지고 구간으로 등록하려하면
	 * Then 실패한다.
	 */
	@DisplayName("새로운 구간 등록 시, 상행역과 하행역 모두 등록되어 있지 않으면 실패한다.")
	@Test
	void addLineSectionFailWithoutAlreadyRegisteredStation() {
		//given
		Station 강남역 = new Station("강남역");
		Station 정자역 = new Station("정자역");

		//when
		//then
		assertThatThrownBy(() -> 신분당선에_구간을_추가한다(강남역, 정자역, 5))
				.isInstanceOf(CannotRegisterWithoutRegisteredStation.class);
	}

	/**
	 * Given 기존 section의 upStation과 일치하는 downStation을 가진 섹션을 추가한다.
	 * When sections를 조회하면
	 * Then 두 개의 구간이 등록되어 있다.
	 */
	@DisplayName("새로운 구간의 downStation이 기존 구간의 upStation과 일치하는 경우 addSection이 성공한다.")
	@Test
	void addSectionsWithDownStationEqualsUpStation() {
		//given
		Station 신사역 = new Station("신사역");
		신분당선에_구간을_추가한다(신사역, 광교역, 3);

		//when
		List<Station> stations = sections.getStations();
		List<Section> sectionsResponse = sections.getSections();

		//then
		assertAll(
				() -> assertThat(stations).hasSize(3),
				() -> assertThat(stations).containsExactly(신사역, 광교역, 판교역),
				() -> assertThat(sectionsResponse.get(0).getDistance()).isEqualTo(3),
				() -> assertThat(sectionsResponse.get(1).getDistance()).isEqualTo(10)
		);
	}

	/**
	 * Given sections에 section 2개를 추가한다.
	 * When sections에서 getStation을 하면
	 * Then sections가 가지고 있는 모든 Station이 반환된다.
	 */
	@DisplayName("getStations 메서드를 검증한다")
	@Test
	void getStations() {
		//given
		Station 정자역 = new Station("정자역");
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선에_구간을_추가한다(광교역, 정자역, 5);
		신분당선에_구간을_추가한다(광교역, 광교중앙역, 2);

		//when
		List<Station> stations = sections.getStations();

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
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선에_구간을_추가한다(광교역, 광교중앙역, 2);

		//when
		sections.removeSection(판교역);

		//then
		List<Section> response = sections.getSections();
		assertThat(response).hasSize(1);
	}

	/**
	 * Given sections에 section을 추가한다.
	 * When sections에서 가운데에 있는 역을 제거하면
	 * Then 성공한다.
	 */
	@DisplayName("구간 가운데에 있는 역을 제거할 수 있다.")
	@Test
	void removeSectionInTheMiddle() {
		//given
		Station 광교중앙역 = new Station("광교중앙역");
		신분당선에_구간을_추가한다(광교역, 광교중앙역, 2);

		//when
		sections.removeSection(광교중앙역);

		//then
		List<Section> sections = this.sections.getSections();
		List<Station> stations = this.sections.getStations();

		assertAll(
				() -> assertThat(sections).hasSize(1),
				() -> assertThat(sections.get(0).getDistance()).isEqualTo(광교역_판교역_거리),
				() -> assertThat(stations).containsExactly(광교역, 판교역)
		);
	}

	/**
	 * When section이 한 개가 있는 sections에서 삭제를 시도하면
	 * Then CannotRemoveLastSectionException이 발생한다.
	 */
	@DisplayName("구간이 하나인 노선의 구간을 삭제할 수 없다.")
	@Test
	void removeSectionsFailOnLastSection() {
		//when
		//then
		assertAll(
				() -> assertThatThrownBy(() -> sections.removeSection(광교역))
				.isInstanceOf(CannotRemoveLastSectionException.class),
				() -> assertThatThrownBy(() -> sections.removeSection(광교역))
				.isInstanceOf(CannotRemoveLastSectionException.class)
		);
	}

	// When 노선에 등록되어 있지 않은 역으로 구간을 삭제하려하면
	// Then IllegalArgumentException 발생한다.
	@DisplayName("등록되어있지 않은 역을 제거하려면 실패한다.")
	@Test
	void removeSectionFailOnNotRegisteredSection() {
	    //when
	    //then
		assertThatThrownBy(() -> sections.removeSection(new Station("등록되어 있지 않은 역")))
				.isInstanceOf(CannotRemoveSectionException.class);
	}

	private void 신분당선에_구간을_추가한다(Station upStation, Station downStation, int distance) {
		sections.add(신분당선, upStation, downStation, distance);
	}
}