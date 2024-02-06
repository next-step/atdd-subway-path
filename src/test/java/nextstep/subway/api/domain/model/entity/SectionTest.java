package nextstep.subway.api.domain.model.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author : Rene Choi
 * @since : 2024/02/03
 */
class SectionTest {

	@Test
	@DisplayName("Section 객체 생성 - 성공 케이스")
	void sectionConstruction_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Long distance = 10L;

		// When
		Section section = Section.of(upStation, downStation, distance);

		// Then
		assertNotNull(section);
		assertEquals(upStation, section.getUpStation());
		assertEquals(downStation, section.getDownStation());
		assertEquals(distance, section.getDistance());
	}

	@Test
	@DisplayName("하행 종점역 판별 - 성공 케이스")
	void isDownEndStation_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Long distance = 10L;
		Section section = Section.of(upStation, downStation, distance);
		when(downStation.getId()).thenReturn(1L);

		// When
		boolean result = section.isDownEndStation(1L);

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("상행 종점역 판별 - 성공 케이스")
	void isUpEndStation_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Long distance = 10L;
		Section section = Section.of(upStation, downStation, distance);
		when(upStation.getId()).thenReturn(1L);

		// When
		boolean result = section.isUpEndStation(1L);

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("임의의 역이 섹션에 포함되는지 판별 - 성공 케이스")
	void isAnyStation_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Long distance = 10L;
		Section section = Section.of(upStation, downStation, distance);
		when(upStation.getId()).thenReturn(1L);
		when(downStation.getId()).thenReturn(2L);

		// When
		boolean resultForUpStation = section.isAnyStation(1L);
		boolean resultForDownStation = section.isAnyStation(2L);

		// Then
		assertTrue(resultForUpStation);
		assertTrue(resultForDownStation);
	}

	@Test
	@DisplayName("마지막 구간 삭제 - 성공 케이스")
	void removeLastSection_success() {
		// Given
		Station upStation = new Station(1L, "상행역");
		Station downStation = new Station(2L, "하행역");
		Long distance = 10L;
		Section section = new Section(1L, upStation, downStation, distance);
		Sections sections = new Sections(new TreeSet<>(Set.of(section)));

		// When
		sections.removeLastSection();

		// Then
		assertTrue(sections.getSections().isEmpty());
	}

	@Test
	@DisplayName("역 목록 파싱 - 성공 케이스")
	void parseStations_success() {
		// Given
		Station upStation1 = new Station(1L, "상행역1");
		Station downStation1 = new Station(2L, "하행역1");
		Station upStation2 = new Station(3L, "상행역2");
		Station downStation2 = new Station(4L, "하행역2");
		Long distance = 10L;
		Section section1 = Section.of(upStation1, downStation1, distance);
		section1.setId(1L);
		Section section2 = Section.of(upStation2, downStation2, distance);
		section2.setId(2L);
		Sections sections = new Sections(new TreeSet<>(Set.of(section1, section2)));

		// When
		List<Station> result = sections.parseStations();

		// Then
		assertEquals(4, result.size());
	}

	@Test
	@DisplayName("두 역이 같은지 판별 - 성공 케이스")
	void isBothStationSame_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Long distance = 10L;
		Section section = Section.of(upStation, downStation, distance);

		// When
		boolean result = section.isBothStationSame(upStation, downStation);

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("섹션 사이에 삽입 가능 여부 - 성공 케이스")
	void canInsertBetween_success() {
		// Given
		Station existingUpStation = new Station(1L, "강남역");
		Station existingDownStation = new Station(2L, "서초역");
		Section existingSection = new Section(1L, existingUpStation, existingDownStation, 10L); // 강남 - 서초

		Station newUpStation = new Station(3L, "신촌역");
		Station newDownStation = existingDownStation;
		Section newSection = new Section(2L, newUpStation, newDownStation, 5L); // 신촌 - 서초

		// When
		boolean result = existingSection.canInsertBetween(newSection);

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("섹션 사이에 삽입 불가능 여부 - 실패 케이스")
	void canInsertBetween_failure() {
		// Given
		Station existingUpStation = new Station(1L, "강남역");
		Station existingDownStation = new Station(2L, "서초역");
		Section existingSection = new Section(1L, existingUpStation, existingDownStation, 10L); // 강남 - 서초

		Station newUpStation = new Station(3L, "신촌역");
		Station newDownStation = new Station(4L, "홍대입구역");
		Section newSection = new Section(2L, newUpStation, newDownStation, 5L); // 신촌 - 홍대입구

		// When
		boolean result = existingSection.canInsertBetween(newSection);

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("상행역 일치 시 섹션 조정 - 성공 케이스")
	void adjustForUpMatch_success() {
		// Given
		Station upStation = new Station(1L, "상행역");
		Station downStation = new Station(2L, "하행역");
		Station newDownStation = new Station(3L, "새 하행역");
		Long distance = 10L;
		Section existingSection = new Section(1L, upStation, downStation, distance);
		Section newSection = new Section(2L, upStation, newDownStation, 5L);

		// When
		Section adjustedSection = existingSection.adjustForUpMatch(newSection);

		// Then
		assertEquals(newDownStation, adjustedSection.getUpStation());
		assertEquals(downStation, adjustedSection.getDownStation());
		assertEquals(5L, adjustedSection.getDistance());
	}

	@Test
	@DisplayName("하행역 일치 시 섹션 조정 - 성공 케이스")
	void adjustForDownMatch_success() {
		// Given
		Station upStation = new Station(1L, "상행역");
		Station downStation = new Station(2L, "하행역");
		Station newUpStation = new Station(3L, "새 상행역");
		Long distance = 10L;
		Section existingSection = new Section(1L, upStation, downStation, distance);
		Section newSection = new Section(2L, newUpStation, downStation, 5L);

		// When
		Section adjustedSection = existingSection.adjustForDownMatch(newSection);

		// Then
		assertEquals(upStation, adjustedSection.getUpStation());
		assertEquals(newUpStation, adjustedSection.getDownStation());
		assertEquals(5L, adjustedSection.getDistance());
	}

	@Test
	@DisplayName("섹션 삽입 검증 - 성공 케이스")
	void validateInsertion_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Long distance = 10L;
		Section existingSection = Section.of(upStation, downStation, distance);

		Station newUpStation = mock(Station.class);
		Station newDownStation = mock(Station.class);
		Section newSection = Section.of(newUpStation, newDownStation, 5L);

		// When & then
		assertDoesNotThrow(() -> existingSection.validateInsertion(newSection));
	}

	@Test
	@DisplayName("구간 거리 업데이트 - 성공 케이스")
	void updateDistance_success() {
		// Given
		Station upStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Section section = Section.of(upStation, downStation, 10L);
		Long newDistance = 15L;

		// When
		section.setDistance(newDistance);

		// Then
		assertEquals(newDistance, section.getDistance());
	}

	@Test
	@DisplayName("상행역 업데이트 - 성공 케이스")
	void updateUpStation_success() {
		// Given
		Station originalUpStation = mock(Station.class);
		Station newUpStation = mock(Station.class);
		Station downStation = mock(Station.class);
		Section section = Section.of(originalUpStation, downStation, 10L);

		// When
		section.setUpStation(newUpStation);

		// Then
		assertEquals(newUpStation, section.getUpStation());
	}

	@Test
	@DisplayName("하행역 업데이트 - 성공 케이스")
	void updateDownStation_success() {
		// Given
		Station upStation = mock(Station.class);
		Station originalDownStation = mock(Station.class);
		Station newDownStation = mock(Station.class);
		Section section = Section.of(upStation, originalDownStation, 10L);

		// When
		section.setDownStation(newDownStation);

		// Then
		assertEquals(newDownStation, section.getDownStation());
	}

	@Test
	@DisplayName("상행 종점역 업데이트 - 성공 케이스")
	void updateUpEndStation_success() {
		// Given
		Station upStation = new Station(1L, "기존 상행역");
		Station newUpStation = new Station(3L, "새 상행역");
		Station downStation = new Station(2L, "하행역");
		Long distance = 10L;
		Section section = new Section(1L, upStation, downStation, distance);

		// When
		section.setUpStation(newUpStation);

		// Then
		assertEquals(newUpStation, section.getUpStation());
	}

	@Test
	@DisplayName("하행 종점역 업데이트 - 성공 케이스")
	void updateDownEndStation_success() {
		// Given
		Station upStation = new Station(1L, "상행역");
		Station downStation = new Station(2L, "기존 하행역");
		Station newDownStation = new Station(3L, "새 하행역");
		Long distance = 10L;
		Section section = new Section(1L, upStation, downStation, distance);

		// When
		section.setDownStation(newDownStation);

		// Then
		assertEquals(newDownStation, section.getDownStation());
	}

	@Test
	@DisplayName("역 거리 조정 - 성공 케이스")
	void adjustDistance_success() {
		// Given
		Station upStation = new Station(1L, "상행역");
		Station downStation = new Station(2L, "하행역");
		Long originalDistance = 10L;
		Section section = new Section(1L, upStation, downStation, originalDistance);
		Long adjustedDistance = 15L;

		// When
		section.setDistance(adjustedDistance);

		// Then
		assertEquals(adjustedDistance, section.getDistance());
	}

	@Test
	@DisplayName("Section 동등성 비교 테스트 - 성공 케이스")
	void sectionEqualsAndHashCode_success() {
		// Given
		Station upStation = new Station(1L, "UpStation");
		Station downStation = new Station(2L, "DownStation");
		Long distance = 10L;
		Section section1 = Section.of(upStation, downStation, distance);
		Section section2 = Section.of(upStation, downStation, distance);

		// When & Then
		assertEquals(section1, section2);
		assertEquals(section1.hashCode(), section2.hashCode());
	}

	@Test
	@DisplayName("Section compareTo 메서드 테스트 - 성공 케이스")
	void sectionCompareTo_success() {
		// Given
		Station upStation1 = new Station(1L, "UpStation1");
		Station downStation1 = new Station(2L, "DownStation1");
		Section section1 = Section.of(upStation1, downStation1, 10L);

		Station upStation2 = new Station(2L, "UpStation2");
		Station downStation2 = new Station(3L, "DownStation2");
		Section section2 = Section.of(upStation2, downStation2, 10L);

		// When
		int result = section1.compareTo(section2);

		// Then
		assertTrue(result < 0);
	}

}