package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.CannotAddSectionException;
import nextstep.subway.line.domain.exception.CannotDeleteSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private static final Line LINE = new Line("신분당선", "red");

    @DisplayName("구간이 하나도 없으면 구간을 추가할 수 있다.")
    @Test
    void 구간_추가1() {
        // given
        Sections sections = new Sections();

        // when
        sections.add(new Section(LINE, 1L, 2L, 6));

        // then
        assertThat(sections.getOrderedStationIds()).containsExactly(1L, 2L);
    }

    @DisplayName("새로운 역을 상행이나 하행 종점으로 등록할 경우 구간이 추가된다.")
    @Test
    void 구간_추가2() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(LINE, 2L, 3L, 6));

        // when
        sections.add(new Section(LINE, 1L, 2L, 6));
        sections.add(new Section(LINE, 3L, 4L, 6));

        // then
        assertThat(sections.getOrderedStationIds()).containsExactly(1L, 2L, 3L, 4L);
    }

    @DisplayName("기존 구간 사이에 새로운 구간을 등록할 경우 기존 구간의 길이가 줄어든다.")
    @Test
    void 구간_추가3() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(LINE, 1L, 3L, 7));

        // when
        sections.add(new Section(LINE, 2L, 3L, 3));

        // then
        assertThat(sections.getOrderedStationIds()).containsExactly(1L, 2L, 3L);

        assertThat(sections.getSections()).extracting("distance").contains(3, 4);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void 구간_추가_예외1() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(LINE, 1L, 2L, 6));

        // when + then
        assertThatThrownBy(() -> sections.add(new Section(LINE, 1L, 2L, 5)))
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 구간을 추가할 수 없습니다.");
    }

    @DisplayName("노선에 구간이 존재할 때 추가하려는 구간의 상행역과 하행역 둘 중 하나도 노선에 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void 구간_추가_예외2() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(LINE, 1L, 2L, 6));

        // when + then
        assertThatThrownBy(() -> sections.add(new Section(LINE, 3L, 4L, 6)))
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
    }

    @DisplayName("지하철 노선에 구간이 둘 이상 있을 때 상행역이나 하행역을 제거할 수 있다.")
    @Test
    void 구간_제거1() {
        // given (1 > 2 > 3 > 4 > 5)
        Sections sections = new Sections();

        sections.add(new Section(LINE, 1L, 2L, 1));
        sections.add(new Section(LINE, 2L, 3L, 1));
        sections.add(new Section(LINE, 3L, 4L, 1));
        sections.add(new Section(LINE, 4L, 5L, 1));

        // when
        sections.removeSection(1L);

        // then (2 > 3 > 4 > 5)
        assertThat(sections.getOrderedStationIds()).containsExactly(2L, 3L, 4L, 5L);
        assertThat(sections.getSections()).extracting("distance").contains(1, 1, 1);

        // when
        sections.removeSection(5L);

        // then (2 > 3 > 4)
        assertThat(sections.getOrderedStationIds()).containsExactly(2L, 3L, 4L);
        assertThat(sections.getSections()).extracting("distance").contains(1, 1, 1);
    }

    @DisplayName("지하철 노선에 구간이 둘 이상 있을 때 구간을 제거하면 구간이 재배치 되고 길이가 수정된다.")
    @Test
    void 구간_제거2() {
        // given (1 > 2 > 3 > 4 > 5)
        Sections sections = new Sections();

        sections.add(new Section(LINE, 1L, 2L, 1));
        sections.add(new Section(LINE, 2L, 3L, 1));
        sections.add(new Section(LINE, 3L, 4L, 1));
        sections.add(new Section(LINE, 4L, 5L, 1));

        // when
        sections.removeSection(2L);

        // then (1 >> 3 > 4 > 5)
        assertThat(sections.getOrderedStationIds()).containsExactly(1L, 3L, 4L, 5L);
        assertThat(sections.getSections()).extracting("distance").contains(2, 1, 1);

        // when
        sections.removeSection(4L);

        // then (1 >> 3 >> 5)
        assertThat(sections.getOrderedStationIds()).containsExactly(1L, 3L, 5L);
        assertThat(sections.getSections()).extracting("distance").contains(2, 2);

        // when
        sections.removeSection(3L);

        // then (1 >>>> 5)
        assertThat(sections.getOrderedStationIds()).containsExactly(1L, 5L);
        assertThat(sections.getSections()).extracting("distance").contains(4);
    }

    @DisplayName("지하철 노선에서 구간이 하나면 제거할 수 없다")
    @Test
    void 구간_제거_예외1() {
        // given
        Sections sections = new Sections();
        Long upStationId = 1L;
        Long downStationId = 2L;

        sections.add(new Section(LINE, upStationId, downStationId, 6));

        // when + then
        assertThatThrownBy(() -> sections.removeSection(downStationId))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage("구간이 둘 이상이어야 역을 제거할 수 있습니다.");
    }

    @DisplayName("지하철 노선에 등록되어있지 않은 역을 제거할 수 없다.")
    @Test
    void 구간_제거_예외2() {
        // given
        Sections sections = new Sections();

        sections.add(new Section(LINE, 1L, 2L, 6));
        sections.add(new Section(LINE, 2L, 3L, 6));

        // when + then
        assertThatThrownBy(() -> sections.removeSection(4L))
                .isInstanceOf(CannotDeleteSectionException.class)
                .hasMessage("등록되어 있지 않은 역을 제거할 수 없습니다.");
    }
}
