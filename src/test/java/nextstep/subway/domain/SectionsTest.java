package nextstep.subway.domain;

import nextstep.subway.exception.AlreadyRegisteredException;
import nextstep.subway.exception.CannotInsertLongerSectionException;
import nextstep.subway.exception.CannotInsertSameDistanceSectionException;
import nextstep.subway.exception.CannotRegisterWithoutRegisteredStation;
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

	private Section 광교역_판교역;

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

		광교역_판교역 = 신분당선_구간을_생성한다(광교역, 판교역, 광교역_판교역_거리);
		sections = new Sections();
		sections.add(광교역_판교역);
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
		Section insertSection = 신분당선_구간을_생성한다(광교역, 광교중앙역, 3);
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
		Section insertSection = 신분당선_구간을_생성한다(광교역, 광교중앙역, 15);

		//then
		assertThatThrownBy(() -> sections.add(insertSection))
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
		Section sameDistanceSection = 신분당선_구간을_생성한다(광교역, 광교중앙역, 광교역_판교역_거리);

		//then
		assertThatThrownBy(() -> sections.add(sameDistanceSection))
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
		Section insertSection = 신분당선_구간을_생성한다(판교역, 정자역, 5);
		sections.add(insertSection);

		//when
		Section 오류발생구간 = 신분당선_구간을_생성한다(광교역, 정자역, 20);

		//then
		assertThatThrownBy(() -> sections.add(오류발생구간))
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
		Section insertSection = 신분당선_구간을_생성한다(강남역, 정자역, 5);

		//when
		//then
		assertThatThrownBy(() -> sections.add(insertSection))
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
		Section insertSection = 신분당선_구간을_생성한다(신사역, 광교역, 3);
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
	 * Given sections에 section 2개를 추가한다.
	 * When sections에서 getStation을 하면
	 * Then sections가 가지고 있는 모든 Station이 반환된다.
	 */
	@DisplayName("getStations 메서드를 검증한다")
	@Test
	void getStations() {
		//given
		sections.add(신분당선_구간을_생성한다(광교역, new Station("정자역"), 5));
		sections.add(신분당선_구간을_생성한다(광교역, new Station("광교중앙역"), 2));

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
		sections.add(신분당선_구간을_생성한다(광교역, 광교중앙역, 2));

	    //when
		sections.removeSection(광교중앙역);

	    //then
		List<Section> response = sections.getSections();
		assertThat(response).hasSize(1);
	}

	/**
	 * When section이 한 개가 있는 sections에서 upstaion으로 삭제를 시도하면
	 * Then IllegalArgumentException이 발생한다.
	 */
	@DisplayName("UpStation으로 섹션을 삭제할 수 없다.")
	@Test
	void removeSectionsFail() {
		//when
		//then
		assertThatThrownBy(() -> sections.removeSection(광교역))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Section 신분당선_구간을_생성한다(Station upStation, Station downStation, Integer distance) {
		return new Section(신분당선, upStation, downStation, distance);
	}
}