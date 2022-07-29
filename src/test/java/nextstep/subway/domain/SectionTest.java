package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

	@DisplayName("상행역 일치 테스트")
	@Test
	void isSameUpStation() {

		Station upStation = new Station("이매역");
		Section section = new Section(upStation, new Station("서현역"), 10);
		Section targetSection = new Section(upStation, new Station("정자역"), 10);

		//when
		boolean isSameUpStation = section.isSameUpStation(targetSection);

		//then
		assertThat(isSameUpStation).isTrue();

	}

	@DisplayName("구간 사이의 거리 비교 테스트")
	@Test
	void isLongerDistanceTest() {

		Section section = new Section(new Station("이매역"), new Station("서현역"), 10);
		Section targetSection = new Section(new Station("이매역"), new Station("정자역"), 15);

		//when
		boolean isSameUpStation = targetSection.isLongerDistance(section);

		//then
		assertThat(isSameUpStation).isTrue();

	}

	@DisplayName("상행역이 같은지 테스트")
	@Test
	void isSameUpStationWithStation() {

		//given
		Station upStation = new Station("이매역");
		Section section = new Section(upStation, new Station("서현역"), 10);

		//when
		boolean isSameUpStation = section.isSameUpStation(upStation);

		//then
		assertThat(isSameUpStation).isTrue();
	}
}
