package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

class LineTest {
	@DisplayName("구간 추가")
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

	@DisplayName("구간 조회")
	@Test
	void getStations() {
	}

	@DisplayName("구간 삭제")
	@Test
	void removeSection() {
	}
}
