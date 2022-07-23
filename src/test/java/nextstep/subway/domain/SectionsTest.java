package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 일급 객체 단위 테스트")
class SectionsTest {

	private Line line;
	private Station upStation;
	private Station downStation;
	private Section section;

	@BeforeEach
	void setUp(){
		line = new Line("분당선", "yellow");
		upStation = new Station("서현역");
		downStation = new Station("야탑역");
		section = new Section(upStation, downStation, 10);
	}

	@DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가")
	@Test
	void addSectionWithUpStation(){
	
		//given
		line.addSection(section);
		Sections sections = Sections.from(line.getSections());
		Station imaeStation = new Station("이매역");
		Section newSection = new Section(upStation, imaeStation, 5);

		//when
		sections.addSection(newSection);

		//then
		assertThat(line.getSections()).hasSize(2);
		assertThat(line.getStations()).contains(upStation, downStation, imaeStation);
		assertThat(line.getSections()).containsExactly(section, newSection);
	}
	 
	@DisplayName("추가하려는 구간의 상행역을 포함하고 있는지 확인하는 메소드 테스트")
	@Test
	void isContainsUpStation(){

		//given
		line.addSection(section);
		Sections sections = Sections.from(line.getSections());
		Section section = new Section(upStation, new Station("이매역"), 5);

		//when
		Optional<Section> result = sections.getSameUpStationSection(section);

		//then
		assertThat(result.isPresent()).isTrue();
	}

	@DisplayName("기존 구간을 기준으로 새로운 구간을 추가하는데 새로운 구간의 길이가 기존 구간보다 길경우 에러 발생")
	@Test
	void validateSameUpStationSectionException() {
		//given
		line.addSection(section);
		Sections sections = Sections.from(line.getSections());
		Station imaeStation = new Station("이매역");
		Section newSection = new Section(upStation, imaeStation, 10);

		//when //then
		assertThatThrownBy(() -> sections.addSection(newSection)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(
			"추가하려는 구간의 길이는 기존 구간의 길이와 같거나 길수 없습니다.");

	 }
}
