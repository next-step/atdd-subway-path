package nextstep.subway.unit;

import static nextstep.subway.unit.LineStaticValues.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.domain.Line;

class LineTest {

	Line line;

	@BeforeEach
	void setUp() {
		line = new Line(1L, "신분당선", "yellow");
	}

	@DisplayName("지하철 구간 추가")
	@Test
	void addSection() {
		//when
		line.addSection(강남역, 역삼역, DISTANCE);

		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("마지막 구간 앞에 새로운 구간 추가")
	@Test
	void addSectionTo() {
		//Given
		line.addSection(강남역, 양재역, DISTANCE);
		line.addSection(양재역, 정자역, DISTANCE);

		//when
		line.addSection(양재역, 양재시민의숲역, DISTANCE);

		//then
		//		assertThat(line.getStations()).hasSize(4)
		//			.containsExactly(강남역, 양재역, 양재시민의숲역, 정자역);
	}

	@DisplayName("지하철 구간 조회_2개")
	@Test
	void getStations() {
		//given
		//when

		line.addSection(강남역, 역삼역, DISTANCE);
		line.addSection(역삼역, 선릉역, DISTANCE);
		//then
		assertThat(line.getStations()).hasSize(3);
	}

	@DisplayName("지하철 구간 조회_0개")
	@Test
	void getStationsZero() {
		//when
		//then
		assertThat(line.getStations()).isEmpty();
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

	@DisplayName("지하철 구간 삭제 실패_삭제역이 마지막 하행선역이 아닌경우")
	@Test
	void removeSectionFail1() {
		//when
		line.addSection(강남역, 역삼역, DISTANCE);
		line.addSection(역삼역, 선릉역, DISTANCE);

		//then
		assertThatThrownBy(() -> line.removeSection(역삼역))
			.isInstanceOf(BusinessException.class);

	}

	@DisplayName("노선 이름변경")
	@Test
	void changeName() {
		//given

		//when
		line.changeName(LINE3_NAME);

		//then
		assertThat(line.getName()).isEqualTo(LINE3_NAME);

	}

	@DisplayName("노선 색상 변경")
	@Test
	void changeColor() {
		//given

		//when
		line.changeColor(LINE3_COLOR);

		//then
		assertThat(line.getColor()).isEqualTo(LINE3_COLOR);

	}
}
