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
		line.addSection(강남역, 역삼역, DISTANCE_VALUE_9);

		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("기존구간 사이에 새로운 구간추가_정상")
	@Test
	void addSectionBetweenSection() {
		//Given
		line.addSection(강남역, 양재역, DISTANCE_VALUE_9);
		line.addSection(양재역, 정자역, DISTANCE_VALUE_5);

		//when
		line.addSection(양재역, 양재시민의숲역, DISTANCE_VALUE_1);

		//then
		assertThat(line.getStations()).hasSize(4)
			.containsExactly(강남역, 양재역, 양재시민의숲역, 정자역);
	}

	@DisplayName("기존구간 사이에 새로운 구간추가_길이가 기존구간보다 큰경우")
	@Test
	void addSectionBetweenSectionFailAboutDistance() {

		//when
		line.addSection(강남역, 양재역, DISTANCE_VALUE_9);
		line.addSection(양재역, 정자역, DISTANCE_VALUE_5);

		//then
		assertThatThrownBy(() -> line.addSection(양재역, 양재시민의숲역, DISTANCE_VALUE_9))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("상행 종점에 새로운 구간추가")
	@Test
	void addSectionToTopStation() {
		//Given
		line.addSection(강남역, 양재역, DISTANCE_VALUE_9);
		line.addSection(양재역, 정자역, DISTANCE_VALUE_5);

		//when
		line.addSection(신논현역, 강남역, DISTANCE_VALUE_5);

		//then
		assertThat(line.getStations()).hasSize(4)
			.containsExactly(신논현역, 강남역, 양재역, 정자역);
	}

	@DisplayName("하행 종점에 새로운 구간추가")
	@Test
	void addSectionToBottomStation() {
		//Given
		line.addSection(강남역, 양재역, DISTANCE_VALUE_3);
		line.addSection(양재역, 정자역, DISTANCE_VALUE_5);

		//when
		line.addSection(정자역, 미금역, DISTANCE_VALUE_1);

		//then
		assertThat(line.getStations()).hasSize(4)
			.containsExactly(강남역, 양재역, 정자역, 미금역);
	}

	@DisplayName("기존구간과 동일한 구간 추가")
	@Test
	void addSameSectionInSections() {
		//when
		line.addSection(강남역, 양재역, DISTANCE_VALUE_9);

		//then
		assertThatThrownBy(() -> line.addSection(강남역, 양재역, DISTANCE_VALUE_9))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("기존구간과 동일한 역이 없는 구간 추가")
	@Test
	void addNewStationNotInSections() {
		//when
		line.addSection(강남역, 양재역, DISTANCE_VALUE_9);

		//then
		assertThatThrownBy(() -> line.addSection(신논현역, 미금역, DISTANCE_VALUE_9))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("지하철 구간 조회_2개")
	@Test
	void getStations() {
		//given
		//when

		line.addSection(강남역, 역삼역, DISTANCE_VALUE_9);
		line.addSection(역삼역, 선릉역, DISTANCE_VALUE_1);
		//then
		assertThat(line.getStations()).hasSize(3)
			.containsExactly(강남역, 역삼역, 선릉역);
	}

	@DisplayName("지하철 구간 조회_3개")
	@Test
	void getStationsOf4() {
		//given
		//when

		line.addSection(강남역, 정자역, DISTANCE_VALUE_9);
		line.addSection(강남역, 양재역, DISTANCE_VALUE_5);
		line.addSection(양재역, 양재시민의숲역, DISTANCE_VALUE_1);
		//then
		assertThat(line.getStations()).hasSize(4)
			.containsExactly(강남역, 양재역, 양재시민의숲역, 정자역);
	}

	@DisplayName("지하철 구간 조회_0개")
	@Test
	void getStationsZero() {
		//when
		//then
		assertThat(line.getStations()).isEmpty();
	}

	@DisplayName("지하철 구간 삭제_하행종점")
	@Test
	void removeLastStation() {
		//given
		line.addSection(강남역, 역삼역, DISTANCE_VALUE_1);
		line.addSection(역삼역, 선릉역, DISTANCE_VALUE_3);
		//when
		line.removeSection(선릉역);
		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("지하철 구간 삭제_상행종점")
	@Test
	void removeTopStation() {
		//given
		line.addSection(강남역, 역삼역, DISTANCE_VALUE_1);
		line.addSection(역삼역, 선릉역, DISTANCE_VALUE_3);
		//when
		line.removeSection(강남역);
		//then
		assertThat(line.getSections()).hasSize(1);
	}

	@DisplayName("지하철 구간 삭제_중간역")
	@Test
	void removeMiddleStation() {
		//given
		line.addSection(강남역, 역삼역, DISTANCE_VALUE_1);
		line.addSection(역삼역, 선릉역, DISTANCE_VALUE_3);
		//when
		line.removeSection(역삼역);
		//then
		assertThat(line.getSections()).hasSize(1);
		assertThat(line.getSections().get(0).getDistance())
			.isEqualTo(DISTANCE_VALUE_1 + DISTANCE_VALUE_3);
	}

	@DisplayName("지하철 구간 삭제_등록되지 않은역")
	@Test
	void removeMiddleStationInNotExistingStation() {
		//when
		line.addSection(강남역, 역삼역, DISTANCE_VALUE_1);
		line.addSection(역삼역, 선릉역, DISTANCE_VALUE_3);
		//then
		assertThatThrownBy(() -> line.removeSection(양재역))
			.isInstanceOf(BusinessException.class);
	}

	@DisplayName("지하철 구간 삭제_섹션1개인경우")
	@Test
	void removeStation() {
		//when
		line.addSection(강남역, 역삼역, DISTANCE_VALUE_1);
		//then
		assertThatThrownBy(() -> line.removeSection(강남역))
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
