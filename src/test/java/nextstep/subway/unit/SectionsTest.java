package nextstep.subway.unit;

import static nextstep.subway.common.SectionFixtures.*;
import static nextstep.subway.common.StationFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nextstep.subway.common.StationFixtures;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.SectionAddException;
import nextstep.subway.domain.exception.SectionErrorCode;
import nextstep.subway.domain.exception.SectionRemoveException;

class SectionsTest {

	private Line line;

	private Sections sections;

	@BeforeEach
	void setUp() {
		line = new Line("4호선", "blue");
		sections = new Sections();
	}

	@DisplayName("노선 구간 추가에 성공한다")
	@Test
	void 노선_구간추가에_성공한다() {
		// given
		int distance = 10;

		// when
		sections.addSection(line, 동대문역사문화공원, 충무로, distance);

		// then
		assertThat(sections.getList()).hasSize(1);
	}

	@DisplayName("노선에 포함된 역조회에 성공한다")
	@Test
	void 노선에_포함된_역조회에_성공한다() throws Exception {
		// given
		sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), 5);

		// when
		List<Station> stations = sections.getStations(동대문역사문화공원_ID, 충무로_ID);

		// then
		assertThat(stations)
			.containsExactly(
				withId(동대문역사문화공원, 동대문역사문화공원_ID),
				withId(충무로, 충무로_ID)
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
			() -> assertThat(sections.getStations(동대문_ID, 동대문역사문화공원_ID)).containsExactly(동대문, 동대문역사문화공원)
		);
	}

	@DisplayName("구간제거시 상행종점역과 하행종점역만 있을경우 예외가 발생한다")
	@Test
	void 구간제거시_상행종점역과_하행종점역만_있을경우_예외가_발생한다() throws Exception {
		sections.addSection(line, withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 10);

		assertThatThrownBy(() -> sections.remove(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), 동대문_ID, 동대문역사문화공원_ID))
			.isInstanceOf(SectionRemoveException.class)
			.hasMessage(SectionErrorCode.SINGLE_SECTION.getMessage());

	}

	@DisplayName("구간제거시 제거할 지하철역이 노선에 포함되지않을경우 예외가 발생한다")
	@Test
	void 구간제거시_제거할_지하철역이_노선에_포함되지않을경우_예외가_발생한다() throws Exception {
		sections.addSection(line, withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 10);
		sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), 5);

		assertThatThrownBy(() -> sections.remove(line, withId(서울역, 서울역_ID), 동대문_ID, 충무로_ID))
			.isInstanceOf(SectionRemoveException.class)
			.hasMessage(SectionErrorCode.NOT_INCLUDE_STATION.getMessage());
	}

	@DisplayName("기존구간사이에 새로운 구간추가시 2개의 구간으로 생성된다")
	@ParameterizedTest
	@MethodSource("provideUpAndDownStations")
	void 기존구간사이에_새로운_구간추가시_2개의_구간으로_생성된다(
		Station upStation,
		Station downStation,
		int distance) throws Exception {
		// given
		sections.addSection(line, withId(동대문, 동대문_ID), withId(서울역, 서울역_ID), 30);

		// when
		sections.addSection(line, upStation, downStation, distance);

		// then
		assertThat(sections.getList()).hasSize(2);
	}

	@DisplayName("기존구간사이에_새로운_구간추가시_이미_등록되어있다면_예외가_발생한다")
	@Test
	void 기존구간사이에_새로운_구간추가시_이미_등록되어있다면_예외가_발생한다() throws Exception {
		sections.addSection(line, withId(동대문, 동대문_ID), withId(충무로, 충무로_ID), 10);

		assertThatThrownBy(() -> sections.addSection(line, withId(동대문, 동대문_ID), withId(충무로, 충무로_ID), 10))
			.isInstanceOf(SectionAddException.class)
			.hasMessage(SectionErrorCode.HAVE_STATIONS.getMessage());

	}

	@DisplayName("기존구간사이에 새로운 구간추가시 추가하는 구간길이가 더길다면 예외가 발생한다")
	@Test
	void 기존구간사이에_새로운_구간추가시_추가하는_구간길이가_더길다면_예외가_발생한다() throws Exception {
		sections.addSection(line, withId(동대문, 동대문_ID), withId(충무로, 충무로_ID), 10);

		assertThatThrownBy(
			() -> sections.addSection(
				line,
				withId(동대문, 동대문_ID),
				withId(동대문역사문화공원, 동대문역사문화공원_ID),
				15)
		).isInstanceOf(SectionAddException.class)
			.hasMessage(SectionErrorCode.MORE_LONGER_LENGTH.getMessage());
	}

	@DisplayName("구간이존재할때 구간추가시 추가될 상행역 하행역 둘중 하나도 포함되어있지 않으면 예외가 발생한다")
	@Test
	void 구간이존재할때_구간추가시_추가될_상행역_하행역_둘중_하나도_포함되어있지_않으면_예외가_발생한다() throws Exception {
		sections.addSection(line, withId(동대문, 동대문_ID), withId(서울역, 서울역_ID), 30);

		assertThatThrownBy(
			() -> sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(충무로, 충무로_ID), 10)
		).isInstanceOf(SectionAddException.class)
			.hasMessage(SectionErrorCode.NOT_FOUND_EXISTING_STATION.getMessage());
	}

	@DisplayName("상행 하행 종점역을 제거요청할 경우 다음에 오는역이 종점역이 된다")
	@ParameterizedTest
	@MethodSource("provideRemoveFinalUpAndDownStation")
	void 상행_하행_종점역을_제거요청할_경우_다음에_오는역이_종점역이_된다(Station removeStation) throws Exception {
		// given
		sections.addSection(line, withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 10);
		sections.addSection(line, withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(서울역, 서울역_ID), 10);

		insertSectionIds(sections.getList());

		// when
		sections.remove(line, removeStation, 동대문_ID, 서울역_ID);

		// then
		assertThat(sections.getList()).hasSize(1);
	}

	private static Stream<Arguments> provideUpAndDownStations() throws Exception {
		return Stream.of(
			Arguments.of(withId(동대문, 동대문_ID), withId(충무로, 충무로_ID), 10),
			Arguments.of(withId(동대문, 동대문_ID), withId(동대문역사문화공원, 동대문역사문화공원_ID), 6),
			Arguments.of(withId(동대문역사문화공원, 동대문역사문화공원_ID), withId(서울역, 서울역_ID), 5),
			Arguments.of(withId(충무로, 충무로_ID), withId(서울역, 서울역_ID), 5),
			Arguments.of(withId(서울역, 서울역_ID), withId(숙대입구역, 숙대입구역_ID), 10),
			Arguments.of(withId(혜화역, 혜화역_ID), withId(동대문, 동대문_ID), 10)
		);
	}

	private static Stream<Arguments> provideRemoveFinalUpAndDownStation() throws Exception {
		return Stream.of(
			Arguments.of(withId(서울역, 서울역_ID)),
			Arguments.of(withId(동대문, 동대문_ID))
		);
	}
}
