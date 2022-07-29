package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {
	private Line 신분당선;
	private Station 강남역;
	private Station 양재역;
	private Section section;

	@BeforeEach
	void setUp() {
		신분당선 = new Line("신분당선", "red");

		강남역 = new Station("강남역");
		양재역 = new Station("양재역");

		section = new Section(신분당선, 강남역, 양재역, 10);

		신분당선.addSection(section);
	}

    @DisplayName("라인에 해당하는 지하철역 조회")
	@Test
	void getStations() {
		List<Station> stations = 신분당선.getStations();

		System.out.println(stations.toString());
		assertThat(stations).hasSize(2);
		assertThat(stations).containsExactly(강남역, 양재역);
	}

	@DisplayName("구간 추가")
	@Nested
	class addSection {
		/**
		 * Given 새로운 역을 생성하고
		 * When 새로운 역을 하행 종점으로 구간 추가를 요청 하면
		 * Then 노선에 새로운 역이 하행 종점으로 등록된다.
		 */
		@DisplayName("새로운 역을 하행 종점으로 등록할 경우")
		@Test
		void addSection_성공1() {
			// given
			Station 정자역 = new Station("정자역");

			// when
			신분당선.addSection(new Section(신분당선, 양재역, 정자역, 10));

			// then
			assertThat(신분당선.getStations()).containsExactly(강남역, 양재역, 정자역);
		}

		/**
		 * Given 새로운 역을 생성하고
		 * When 새로운 역을 상행 종점으로 구간 추가를 요청 하면
		 * Then 노선에 새로운 역이 상행 종점으로 등록된다.
		 */
		@DisplayName("새로운 역을 상행 종점으로 등록할 경우")
		@Test
		void addSection_성공2() {
			// given
			Station 정자역 = new Station("정자역");

			// when
			신분당선.addSection(new Section(신분당선, 정자역, 강남역, 10));

			// then
			assertThat(신분당선.getStations()).containsExactly(정자역, 강남역, 양재역);
		}

		/**
		 * Given 새로운 역을 생성하고
		 * When 새로운 역을 중간역으로 구간 추가를 요청 하면
		 * Then 노선 중간에 새로운 역이 등록된다.
		 */
		@DisplayName("역 사이에 새로운 역을 등록할 경우")
		@Test
		void addSection_성공3() {
			// given
			Station 정자역 = new Station("정자역");

			// when
			신분당선.addSection(new Section(신분당선, 강남역, 정자역, 7));

			// then
			assertThat(신분당선.getStations()).containsExactly(강남역, 정자역, 양재역);
		}

		/**
		 * When 기존 역을 새로운 구간으로 추가하면
		 * Then 에러가 발생한다.
		 */
		@DisplayName("예외 1) 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
		@Test
		void addSection_실패1() {
			// when AND then
			assertThatThrownBy(()-> 신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10)))
				.isInstanceOf(IllegalArgumentException.class);
		}

		/**
		 * Given 새로운 역을 2개 생성하고
		 * When 새로운 역으로만 구성된 구간을 추가하면
		 * Then 에러가 발생한다.
		 */
		@DisplayName("예외 2) 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
		@Test
		void addSection_실패2() {
			// given
			Station 정자역 = new Station("정자역");
			Station 판교역 = new Station("판교역");

			// when AND then
			assertThatThrownBy(()-> 신분당선.addSection(new Section(신분당선, 정자역, 판교역, 10)))
				.isInstanceOf(IllegalArgumentException.class);
		}

		/**
		 * Given 새로운 역을 생성하고
		 * When 기존 구간보다 큰 구간을 사이에 등록하면
		 * Then 에러가 발생한다.
		 */
		@DisplayName("예외 3) 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
		@Test
		void addSection_실패3() {
			// given
			Station 정자역 = new Station("정자역");

			// when AND then
			assertThatThrownBy(()-> 신분당선.addSection(new Section(신분당선, 강남역, 정자역, 20)))
				.isInstanceOf(IllegalArgumentException.class);
		}
	}

    @DisplayName("구간 제거")
	@Test
	void removeSection() {
		Station 양재시민의숲역 = new Station("양재시민의숲역");
		Section section2 = new Section(신분당선, 양재역, 양재시민의숲역, 20);
		신분당선.addSection(section2);

		신분당선.removeSection(양재시민의숲역);
		List<Station> stations = 신분당선.getStations();

		assertThat(stations).containsExactly(강남역, 양재역);
	}

	@DisplayName("구간이 하나만 남은 경우 구간을 삭제할 수 없다.")
	@Test
	void removeSectionError() {
		assertThatThrownBy(() -> 신분당선.removeSection(양재역)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("하행 종점역이 아닌경우 구간을 삭제할 수 없다.")
	@Test
	void removeSectionError2() {
		assertThatThrownBy(() -> 신분당선.removeSection(강남역)).isInstanceOf(IllegalArgumentException.class);
	}
}
