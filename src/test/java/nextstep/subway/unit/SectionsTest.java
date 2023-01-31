package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import org.junit.jupiter.api.Test;

import static nextstep.subway.fixture.SectionFixture.createSectionFixTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SectionsTest {

    @Test
    void 구간을_추가_한다() {
        //given
        Section section1 = createSectionFixTrue(1L, 2L);
        Section section2 = createSectionFixTrue(2L, 3L);
        Sections sections = Sections.from(section1);

        //when
        sections.add(section2);

        //then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void 기존_구간의_하행역과_추가하는_상행역이_다르면_추가_실패() {
        //given
        Section section1 = createSectionFixTrue(1L, 2L);
        Section section2 = createSectionFixTrue(3L, 4L);
        Sections sections = Sections.from(section1);

        //when then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(section2));
    }

    @Test
    void 구간에_이미_등록된_역을_추가하면_실패() {
        //given
        Section section1 = createSectionFixTrue(1L, 2L);
        Section section2 = createSectionFixTrue(2L, 1L);
        Sections sections = Sections.from(section1);

        //when then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(section2));
    }

    @Test
    void 노선_구간_삭제_하행역Id로_삭제한다() {
        //given
        long lastDownStationId = 3L;
        Section section1 = createSectionFixTrue(1L, 2L);
        Section section2 = createSectionFixTrue(2L, lastDownStationId);
        Sections sections = Sections.from(section1, section2);

        //when
        sections.removeByStationId(lastDownStationId);

        //then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0)).isEqualTo(section1);
    }

    @Test
    void 노선_구간_삭제_엔티디로_삭제한다() {
        //given
        Section section1 = createSectionFixTrue(1L, 2L);
        Section section2 = createSectionFixTrue(2L, 3L);
        Sections sections = Sections.from(section1, section2);

        //when
        sections.remove(section2);

        //then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0)).isEqualTo(section1);
    }
}