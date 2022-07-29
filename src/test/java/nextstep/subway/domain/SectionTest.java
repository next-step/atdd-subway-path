package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위 테스트")
class SectionTest {

	private Station upStation;
	private Station downStation;
	private Section section;
	private Line line;

	@BeforeEach
	void setUp() {
		line = new Line("분당선", "yellow");
		upStation = new Station("서현역");
		downStation = new Station("이매역");
		section = new Section(line, upStation, downStation, 10);
	}

	@DisplayName("하행 종점 일치 단위 테스트")
	@Test
	void isSameDownStationTest() {
		//given
		Section section = new Section(line, upStation, downStation, 10);

		//when
		boolean isSameDownStation = section.isSameDownStation(downStation);

		//then
		assertThat(isSameDownStation).isTrue();
	}

	@DisplayName("하행 종점 일치 단위 테스트 구간으로")
	@Test
	void isSameDownStationBySectionTest() {

		//given
		Section newSection = new Section(new Station("판교역"), downStation, 15);

		//when
		boolean isSameDownStation = section.isSameDownStation(newSection);

		//then
		assertThat(isSameDownStation).isTrue();

	}

	@DisplayName("상행역 일치 테스트")
	@Test
	void isSameUpStation() {
		//given
		Section newSection = new Section(upStation, new Station("정자역"), 10);

		//when
		boolean isSameUpStation = section.isSameUpStation(newSection);

		//then
		assertThat(isSameUpStation).isTrue();

	}

	@DisplayName("상행역 일치 단위 테스트 구간으로")
	@Test
	void isSameUpStationBySection() {

		//given
		Section newSection = new Section(upStation, new Station("정자역"), 10);

		//when
		boolean isSameUpStation = section.isSameUpStation(newSection);

		//then
		assertThat(isSameUpStation).isTrue();

	}

	@DisplayName("구간 사이의 거리 비교 테스트")
	@Test
	void isLongerDistanceTest() {

		Section targetSection = new Section(new Station("이매역"), new Station("정자역"), 5);

		//when
		boolean isSameUpStation = section.isLongerDistance(targetSection);

		//then
		assertThat(isSameUpStation).isTrue();

	}
}
