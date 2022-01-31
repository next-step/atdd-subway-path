package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.InvalidSectionDeleteException;
import nextstep.subway.exception.InvalidSectionSizeLimitExecption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 삭제에 대한 도메인 단위 테스트")
class LineDeleteTest {

	Station 강남역;
	Station 판교역;
	Station 양재역;
	Line 신분당선;
	Distance 강남_판교_거리 = Distance.valueOf(7);
	Distance 판교_양재_거리 = Distance.valueOf(2);
	Distance 강남_양재_거리 = Distance.valueOf(9);


	/**
	 * Given 신분당선 노선에 강남-판교-양재 역이 순서대로 위치할 경우
	 */
	@BeforeEach
	void setLine() {
		강남역 = new Station("강남역");
		판교역 = new Station("판교역");
		양재역 = new Station("양재역");
		신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 강남_판교_거리);
		Section 판교_양재 = new Section(신분당선, 판교역, 양재역, 판교_양재_거리);
		신분당선.addSection(판교_양재);
	}

	/**
	 * When 노선에 등록되지 않은 구간을 삭제할 경우
	 * Then 예외를 던진다.
	 */
	@DisplayName("노선에 등록되지 않은 구간을 삭제")
	@Test
	void removeExceptionWhenSectionNotExists() {
		final Station 가양역 = new Station("가양역");

		assertThatThrownBy(() -> 신분당선.removeSection(가양역))
				.isInstanceOf(InvalidSectionDeleteException.class);
	}

	/**
	 * Given 노선에 구간이 하나일 경우
	 * When 구간을 삭제할 경우
	 * Then 예외를 던진다.
	 */
	@DisplayName("노선에 구간이 하나일 때 구간 삭제")
	@Test
	void removeExceptionWhenOneSectionLeft() {
		// given
		신분당선.removeSection(양재역);

		// when
		assertThatThrownBy(() -> 신분당선.removeSection(판교역))
				.isInstanceOf(InvalidSectionSizeLimitExecption.class);

	}

	/**
	 * When 중간 역을 기준으로 구간 삭제요청하면
	 * Then 중간 역이 삭제되고 구간이 재배치된다.
	 * Then 새로 배치된 구간의 거리는 이전 두 구간의 거리의 합과 같다.
	 */
	@DisplayName("중간역 기준으로 구간 삭제")
	@Test
	void removeSectionBetweenLastStations() {
		// when
		신분당선.removeSection(판교역);
		// then
		final Section 강남_양재_구간 = 신분당선.getSectionAt(0);

		assertThat(신분당선.getSectionSize()).isEqualTo(1);

		assertThat(강남_양재_구간).extracting(Section::getUpStation).isEqualTo(강남역);
		assertThat(강남_양재_구간).extracting(Section::getDownStation).isEqualTo(양재역);
		assertThat(강남_양재_구간).extracting(Section::getDistance).isEqualTo(강남_양재_거리);
	}

	/**
	 * When 상행 종점역을 기준으로 구간 삭제요청하면
	 * Then 마지막 구간이 삭제된다
	 */
	@DisplayName("하행 종점역 기준으로 구간 삭제")
	@Test
	void removeSectionBasedOnLastUpStation() {
		// when
		신분당선.removeSection(강남역);
		// then
		final Section 판교_양재 = 신분당선.getSectionAt(0);

		assertThat(신분당선.getSectionSize()).isEqualTo(1);

		assertThat(판교_양재).extracting(Section::getUpStation).isEqualTo(판교역);
		assertThat(판교_양재).extracting(Section::getDownStation).isEqualTo(양재역);
		assertThat(판교_양재).extracting(Section::getDistance).isEqualTo(판교_양재_거리);
	}

	/**
	 * When 하행 종점역을 기준으로 구간 삭제요청하면
	 * Then 마지막 구간이 삭제된다
	 */
	@DisplayName("하행 종점역 기준으로 구간 삭제")
	@Test
	void removeSectionBasedOnLastDownStation() {
		// when
		신분당선.removeSection(양재역);
		// then
		final Section 강남_판교 = 신분당선.getSectionAt(0);

		assertThat(신분당선.getSectionSize()).isEqualTo(1);

		assertThat(강남_판교).extracting(Section::getUpStation).isEqualTo(강남역);
		assertThat(강남_판교).extracting(Section::getDownStation).isEqualTo(판교역);
		assertThat(강남_판교).extracting(Section::getDistance).isEqualTo(강남_판교_거리);
	}


}
