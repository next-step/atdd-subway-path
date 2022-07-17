package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

class LineTest {

	static Station 강남역 = new Station(1L, "강남역");
	static Station 역삼역 = new Station(2L, "역삼역");
	static Station 선릉역 = new Station(3L, "선릉역");

	static int DISTANCE = 9;
	Line line;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "신분당선", "color");
	}

	@DisplayName("지하철 구간 추가")
	@Test
	void addSection() {
		//given

		//when
		line.addSection(강남역, 역삼역, DISTANCE);

		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("지하철 구간 조회")
	@Test
	void getStations() {
		//given
		//when

		line.addSection(강남역, 역삼역, DISTANCE);
		line.addSection(역삼역, 선릉역, DISTANCE);
		//then
		assertThat(line.getStations()).hasSize(3);
	}

	@DisplayName("지하철 구간 삭제")
	@Test
	void removeSection() {
		//given

		line.addSection(강남역, 역삼역, DISTANCE);
		line.addSection(역삼역, 선릉역, DISTANCE);
		//when
		line.removeSection(선릉역);
		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("지하철 구간 삭제 실패")
	@Test
	void removeSectionFail1() {
		//given

		//when
		line.addSection(강남역, 역삼역, DISTANCE);
		line.addSection(역삼역, 선릉역, DISTANCE);

		//then
		assertThatThrownBy(() -> line.removeSection(역삼역))
			.isInstanceOf(IllegalArgumentException.class);

	}

	@DisplayName("노선 이름변경")
	@Test
	void changeName() {
		//given

		//when
		line.changeName("3호선");

		//then
		assertThat(line.getName()).isEqualTo("3호선");

	}

	@DisplayName("노선 색상 변경")
	@Test
	void changeColor() {
		//given

		//when
		line.changeColor("blue");

		//then
		assertThat(line.getColor()).isEqualTo("blue");

	}
}
