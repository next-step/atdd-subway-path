package nextstep.subway.domain;

import org.junit.jupiter.api.Test;

import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SectionAddActionTest {
    
    @Test
    void 지하철_노선_중간_추가_액션_생성() {
        Sections sections = Sections.from(createSection(0L, 3L, 10));
        Section newSection = createSection(0L, 2L, 1);

        SectionAction sectionAction = SectionAction.of(sections, newSection);
        assertThat(sectionAction).isEqualTo(SectionAction.ADD_MIDDLE_STATION);
    }

    @Test
    void 지하철_노선_상행구간_추가_액션_생성() {
        Sections sections = Sections.from(createSection(1L, 2L));
        Section newSection = createSection(0L, 1L);

        SectionAction sectionAction = SectionAction.of(sections, newSection);
        assertThat(sectionAction).isEqualTo(SectionAction.ADD_UP_STATION);
    }

    @Test
    void 지하철_노선_하행구간_추가_액션_생셩() {
        Sections sections = Sections.from(createSection(1L, 2L));
        Section newSection = createSection(2L, 3L);

        SectionAction sectionAction = SectionAction.of(sections, newSection);
        assertThat(sectionAction).isEqualTo(SectionAction.ADD_DOWN_STATION);
    }

    @Test
    void 이미_등록된_구간은_추가_불가() {
        Sections sections = Sections.from(createSection(0L, 3L));
        Section newSection = createSection(0L, 3L);

        assertThatIllegalArgumentException().isThrownBy(() -> SectionAction.of(sections, newSection));
    }

    @Test
    void 상행_하행역이_모두_노선에_등록_안되어_있으면_추가_불가() {
        Sections sections = Sections.from(createSection(0L, 3L));
        Section newSection = createSection(1L, 2L);

        assertThatIllegalArgumentException().isThrownBy(() -> SectionAction.of(sections, newSection));
    }
}