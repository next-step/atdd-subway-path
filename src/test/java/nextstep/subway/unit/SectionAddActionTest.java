package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionAction;
import nextstep.subway.domain.Sections;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.*;

class SectionAddActionTest {
    
    @Test
    void 지하철_노선_중간_추가_액션_생성() {
        List<Section> sections = new ArrayList(Arrays.asList(createSection(0L, 3L, 10)));
        Section newSection = createSection(0L, 2L, 1);

        SectionAction sectionAction = SectionAction.of(sections, newSection);
        assertThat(sectionAction).isEqualTo(SectionAction.ADD_MIDDLE_STATION);
    }

    @Test
    void 지하철_노선_상행구간_추가_액션_생성() {
        List<Section> sections = new ArrayList(Arrays.asList(createSection(1L, 2L)));
        Section newSection = createSection(0L, 1L);

        SectionAction sectionAction = SectionAction.of(sections, newSection);
        assertThat(sectionAction).isEqualTo(SectionAction.ADD_UP_STATION);
    }

    @Test
    void 지하철_노선_하행구간_추가_액션_생셩() {
        List<Section> sections = new ArrayList(Arrays.asList(createSection(1L, 2L)));
        Section newSection = createSection(2L, 3L);

        SectionAction sectionAction = SectionAction.of(sections, newSection);
        assertThat(sectionAction).isEqualTo(SectionAction.ADD_DOWN_STATION);
    }

    @Test
    void 이미_등록된_구간은_추가_불가() {
        List<Section> sections = new ArrayList(Arrays.asList(createSection(0L, 3L)));
        Section newSection = createSection(0L, 3L);

        assertThatIllegalArgumentException().isThrownBy(() -> SectionAction.of(sections, newSection));
    }

    @Test
    void 상행_하행역이_모두_노선에_등록_안되어_있으면_추가_불가() {
        List<Section> sections = new ArrayList(Arrays.asList(createSection(0L, 3L)));
        Section newSection = createSection(1L, 2L);

        assertThatIllegalArgumentException().isThrownBy(() -> SectionAction.of(sections, newSection));
    }

    @Test
    void 기존_구간_사이에_역이_추가_된다() {
        //given
        long stationId1 = 1L;
        long stationId2 = 2L;
        long stationId3 = 3L;

        List<Section> sections = new ArrayList(Arrays.asList(createSection(stationId1, stationId3, 7)));
        Section newSection = createSection(stationId1, stationId2, 4);
        SectionAction sectionAction = SectionAction.of(sections, newSection);

        sectionAction.add(sections, newSection);

        assertThat(sections.size()).isEqualTo(2);
        Section section1 = sections.get(0);
        assertStationId(stationId1, stationId2, section1);
        assertThat(section1.getDistance()).isEqualTo(4);

        Section section2 = sections.get(1);
        assertStationId(stationId2, stationId3, section2);
        assertThat(section2.getDistance()).isEqualTo(3);
    }

    @Test
    void 상행구간_추가() {
        long stationId1 = 0L;
        long stationId2 = 1L;
        long stationId3 = 2L;

        List<Section> sections = new ArrayList(Arrays.asList(createSection(stationId2, stationId3)));
        Section newSection = createSection(stationId1, stationId2);
        SectionAction sectionAction = SectionAction.of(sections, newSection);

        sectionAction.add(sections, newSection);

        assertThat(sections.size()).isEqualTo(2);
        assertStationId(stationId1, stationId2, sections.get(0));
        assertStationId(stationId2, stationId3, sections.get(1));
    }

    @Test
    void 하행구간_추가() {
        long stationId1 = 1L;
        long stationId2 = 2L;
        long stationId3 = 3L;

        List<Section> sections = new ArrayList(Arrays.asList(createSection(stationId1, stationId2)));
        Section newSection = createSection(stationId2, stationId3);
        SectionAction sectionAction = SectionAction.of(sections, newSection);

        sectionAction.add(sections, newSection);

        assertThat(sections.size()).isEqualTo(2);
        assertStationId(stationId1, stationId2, sections.get(0));
        assertStationId(stationId2, stationId3, sections.get(1));
    }

    private void assertStationId(long stationId1, long stationId2, Section section) {
        assertThat(section.getUpStationId()).isEqualTo(stationId1);
        assertThat(section.getDownStationId()).isEqualTo(stationId2);
    }
}