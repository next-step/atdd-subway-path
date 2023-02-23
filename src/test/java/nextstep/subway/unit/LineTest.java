package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CannotCreateSectionException;
import nextstep.subway.exception.ErrorMessage;
import nextstep.subway.exception.InvalidArgumentException;

class LineTest {
	@DisplayName("구간 추가 - 새로운 역이 상행 종점역과 하행 종점역 사이로 하는 구간 생성")
	@Test
	void addSectionBetweenUpStationAndDownStation() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 역삼역 = new Station("역삼역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(강남역, 역삼역, 9);

		// Then
		assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
	}

	@DisplayName("구간 추가 - 새로운 역이 상행 종점역과 하행 종점역 사이로 하는 구간 생성 - 기존 구간의 거리 변경 검증")
	@Test
	void addSectionBetweenUpStationAndDownStation_assertDistance() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 역삼역 = new Station("역삼역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(강남역, 역삼역, 9);

		Sections 이호선_구간_리스트 = 이호선.getSections();
		Section 역삼역_선릉역_구간 = 이호선_구간_리스트.getSections().get(1);

		// Then
		assertThat(역삼역_선릉역_구간.getDistance()).isEqualTo(1);
	}

	@DisplayName("구간 추가 예외 - 새로운 역이 상행 종점역과 하행 종점역 사이로 하는 구간의 길이가 기존 구간의 길이보다 크거나 같은 경우 예외 발생")
	@ParameterizedTest
	@ValueSource(ints = {10, 11, 12})
	void addSectionBetweenUpStationAndDownStation_fail(int distance) {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 역삼역 = new Station("역삼역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);

		// Then
		assertThatThrownBy(() -> 이호선.addSection(강남역, 역삼역, distance))
			.isInstanceOf(InvalidArgumentException.class)
			.hasMessage(ErrorMessage.DISTANCE_OF_SECTION_MUST_BE_POSITIVE.getMessage());
	}

	@DisplayName("구간 추가 예외 - 상행역과 하행역 둘 다 포함하지 않는 구간 추가 시 예외 발생")
	@Test
	void addSection_fail_should_be_include_station_or_down_station() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 삼성역 = new Station("삼성역");
		Station 교대역 = new Station("교대역");

		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);

		// Then
		assertThatThrownBy(() -> 이호선.addSection(삼성역, 교대역, 10))
			.isInstanceOf(CannotCreateSectionException.class)
			.hasMessage(ErrorMessage.SHOULD_BE_INCLUDE_UP_STATION_OR_DOWN_STATION.getMessage());
	}

	@DisplayName("구간 추가 예외 - 이미 존재하는 구간을 추가하는 경우 예외 발생")
	@Test
	void addSection_fail_should_exist_new_station() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");

		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);

		// Then
		assertThatThrownBy(() -> 이호선.addSection(강남역, 선릉역, 10))
			.isInstanceOf(CannotCreateSectionException.class)
			.hasMessage(ErrorMessage.SHOULD_EXIST_NEW_STATION.getMessage());
	}

	@DisplayName("구간 추가 - 새로운 역이 상행 종점역으로 하는 구간 생성")
	@Test
	void addSectionToUpStation() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 교대역 = new Station("교대역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(교대역, 강남역, 10);
		// Then
		assertThat(이호선.getStations()).containsExactly(교대역, 강남역, 선릉역);
	}

	@DisplayName("구간 추가 - 새로운 역이 하행 종점역으로 하는 구간 생성")
	@Test
	void addSectionToDownStation() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 삼성역 = new Station("삼성역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(선릉역, 삼성역, 10);
		// Then
		assertThat(이호선.getStations()).containsExactly(강남역, 선릉역, 삼성역);
	}

	@DisplayName("구간 추가 - 노선이 생성 되는 경우")
	@Test
	void addSection() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);
		// Then
		assertThat(이호선.getSections().getSections()).isNotEmpty();
	}

	@DisplayName("노선의 역 조회")
	@Test
	void getStations() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Line 이호선 = new Line("2호선", "green");

		// When
		이호선.addSection(강남역, 선릉역, 10);

		// Then
		assertThat(이호선.getStations()).containsExactly(강남역, 선릉역);
	}

	@DisplayName("구간 삭제")
	@Test
	void removeSection() {
		// Given
		Station 강남역 = new Station("강남역");
		Station 선릉역 = new Station("선릉역");
		Station 삼성역 = new Station("삼성역");

		Line 이호선 = new Line("2호선", "green");
		이호선.addSection(강남역, 선릉역, 10);
		이호선.addSection(선릉역, 삼성역, 20);

		// When
		이호선.removeSection(삼성역);

		// Then
		assertThat(이호선.getStations()).containsExactly(강남역, 선릉역);
	}
}
