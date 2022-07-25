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
        sections.add(new Section(LINE, 1L, 3L, 6));

        // when
        sections.add(new Section(LINE, 2L, 3L, 4));

        // then
        assertThat(sections.stationIds()).containsExactly(1L, 2L, 3L);
        assertThat(sections.getSections()).contains(new Section(LINE, 1L, 2L, 2),
                        new Section(LINE, 2L, 3L, 4));
    }

    @DisplayName("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없다.")
    @Test
    void 구간_추가_예외1() {
        // given
        int distance = 6;

        Sections sections = new Sections();
        sections.add(new Section(LINE, 1L, 3L, distance));

        // when + then
        assertThatThrownBy(() -> sections.add(new Section(LINE, 1L, 2L, distance)))
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없습니다.");

        assertThatThrownBy(() -> sections.add(new Section(LINE, 2L, 3L, distance)))
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void 구간_추가_예외2() {
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
    void 구간_추가_예외3() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(LINE, 1L, 2L, 6));

        // when + then
        assertThatThrownBy(() -> sections.add(new Section(LINE, 3L, 4L, 6)))
                .isInstanceOf(CannotAddSectionException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
    }

    @Test
    void 구간_삭제() {
        // given
        Sections sections = new Sections();
        Long upStationId = 1L;
        Long downStationId = 2L;

        sections.add(new Section(LINE, upStationId, downStationId, 6));

        // when
        sections.removeSection(downStationId);

        // then
        assertThat(sections.getOrderedStationIds()).isEmpty();
    }

    @Test
    void 구간_삭제_종점이_아니면_예외() {
        // given
        Sections sections = new Sections();
        Long upStationId = 1L;
        Long downStationId = 2L;

        sections.add(new Section(LINE, upStationId, downStationId, 6));

        // when + then
        assertThatThrownBy(() -> sections.removeSection(upStationId))
                .isInstanceOf(CannotDeleteSectionException.class);
    }
}
