package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 단위 테스트")
class SectionTest {
	@DisplayName("하행 종점 일치 단위 테스트")
	@Test
	void isSameDownStationTest() {
	    //given
		Line line = new Line("분당선", "yellow");
		Station downStation = new Station("이매역");
		Section section = new Section(line, new Station("서현역"), downStation, 10);

	    //when
		boolean isSameDownStation = section.isSameDownStation(downStation);

	    //then
		assertThat(isSameDownStation).isTrue();
	 }
}
