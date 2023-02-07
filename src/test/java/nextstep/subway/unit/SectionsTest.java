package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class SectionsTest {

    @Test
    void 구간을_추가_한다() {
        //given
        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(2L, 3L);
        Sections sections = Sections.from(section1);

        //when
        sections.add(section2);

        //then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void 기존_구간_사이에_역이_추가_된다() {
        //given
        Sections sections = Sections.from(createSection(1L, 3L, 10));
        Section newSection = createSection(1L, 2L, 3);

        sections.addMiddleStation(newSection);

        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void 상행구간_추가() {
        Sections sections = Sections.from(createSection(1L, 2L));
        Section newSection = createSection(0L, 1L);

        sections.addUpStation(newSection);

        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void 하행구간_추가() {
        Sections sections = Sections.from(createSection(1L, 2L));
        Section newSection = createSection(2L, 3L);

        sections.addDownStation(newSection);

        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void 구간에_이미_등록된_역을_추가하면_실패() {
        //given
        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(1L, 2L);
        Sections sections = Sections.from(section1);

        //when then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(section2));
    }

    @Test
    void 노선_마지막_구간_삭제() {
        //given
        long lastDownStationId = 3L;
        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(2L, lastDownStationId);
        Sections sections = Sections.from(section1, section2);

        //when
        sections.remove(section2);

        //then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0)).isEqualTo(section1);
    }

    @Test
    void 노선_구간_삭제_엔티디로_삭제한다() {
        //given
        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(2L, 3L);
        Sections sections = Sections.from(section1, section2);

        //when
        sections.remove(section2);

        //then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0)).isEqualTo(section1);
    }

    @Test
    void 역_조회() {
        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(2L, 3L);
        Sections sections = Sections.from(section1, section2);

        List<Station> stations = sections.getStations();

        assertThat(stations).hasSize(3);
    }
}