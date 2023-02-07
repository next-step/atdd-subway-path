package nextstep.subway.unit;

import static nextstep.subway.common.LineFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import nextstep.subway.common.StationFixtures;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.SectionErrorCode;
import nextstep.subway.domain.exception.SectionRemoveException;

@ExtendWith(MockitoExtension.class)
class SectionsTest {

	@Mock
	private Line line;

	private Sections sections;

	@BeforeEach
	void setUp() {
		sections = new Sections();
	}

	@DisplayName("구간 추가에 성공한다")
	@Test
	void addSection() {
		// given
		int distance = 10;

		// when
		sections.addSection(line, 동대문역사문화공원, 충무로, distance);

		// then
		assertThat(sections.getList()).hasSize(1);
	}

	@DisplayName("전체 역 조회에 성공한다")
	@Test
	void getStations() throws Exception {
		// given
		sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), 5);

		// when
		List<Station> stations = sections.getStations();

		// then
		assertThat(stations)
			.containsExactly(
				동대문,
				동대문역사문화공원,
				충무로
			);
	}

	@DisplayName("노선 생성 시 초기 구간을 생성한다")
	@Test
	void createInitializeSection() throws Exception {
		// given
		Station 동대문 = withId(StationFixtures.동대문, 동대문_ID);
		Station 동대문역사문화공원 = withId(StationFixtures.동대문역사문화공원, 동대문역사문화공원_ID);

		// when
		sections.createInitialLineSection(동대문, 동대문역사문화공원, 10, line);

		// then
		assertAll(
			() -> assertThat(sections.getList()).hasSize(1),
			() -> assertThat(sections.getStations()).containsExactly(동대문, 동대문역사문화공원)
		);
	}

	@DisplayName("구간이 한개 이상일때 마지막 구간 제거에 성공한다")
	@Test
	void 구간이_한개_이상일때_마지막_구간_제거에_성공한다() throws Exception {
		// given
		Sections sections = new Sections();
		Long downStationId = 충무로_ID;

		sections.addSection(line, withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 10);
		sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), 5);
		insertIdInSections(sections.getList());

		// when
		sections.remove(충무로, downStationId);

		// then
		assertThat(sections.getList()).hasSize(1);
	}

	@DisplayName("구간제거시 상행종점역과 하행종점역만 있을경우 예외가 발생한다")
	@Test
	void 구간제거시_상행종점역과_하행종점역만_있을경우_예외가_발생한다() throws Exception {
		Sections sections = new Sections();
		sections.addSection(line, withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 10);

		assertThatThrownBy(() -> sections.remove(동대문역사문화공원, 동대문역사문화공원_ID))
			.isInstanceOf(SectionRemoveException.class)
			.hasMessage(SectionErrorCode.SINGLE_SECTION.getMessage());

	}

	private void insertIdInSections(List<Section> sections) {
		for (int i = 1; i <= sections.size(); i++) {
			Section section = sections.get(i - 1);
			Field idField = ReflectionUtils.findField(section.getClass(), "id");
			ReflectionUtils.makeAccessible(idField);
			ReflectionUtils.setField(idField, section, (long)i);
		}
	}
}
