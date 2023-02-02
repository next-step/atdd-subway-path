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
        Section 구간1 = createSection(1L, 3L, 10);
        Section 구간2 = createSection(1L, 2L, 5);
        Sections 구간들 = Sections.from(구간1);
        int 새구간1거리 = 구간1.getDistance() - 구간2.getDistance();
        int 새구간2거리 = 구간2.getDistance();

        //when
        구간들.add(구간2);

        //then
        assertThat(구간들.size()).isEqualTo(2);

        assertThat(구간들.get(0).getUpStationId()).isEqualTo(1L);
        assertThat(구간들.get(0).getDownStationId()).isEqualTo(2L);
        assertThat(구간들.get(0).getDistance()).isEqualTo(새구간1거리);

        assertThat(구간들.get(1).getUpStationId()).isEqualTo(2L);
        assertThat(구간들.get(1).getDownStationId()).isEqualTo(3L);
        assertThat(구간들.get(1).getDistance()).isEqualTo(새구간2거리);
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