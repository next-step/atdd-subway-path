package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

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
    void 구간에_이미_등록된_역을_추가하면_실패() {
        //given
        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(1L, 2L);
        Sections sections = Sections.from(section1);

        //when then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(section2));
    }

    @Test
    void 역_조회() {
        long id0 = 0L;
        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;
        long id4 = 4L;

        Sections sections = Sections.from(
                createSection(id1, id3, 10),
                createSection(id1, id2, 3),
                createSection(id0, id1),
                createSection(id3, id4));

        List<Long> ids = sections.getStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        assertThat(ids)
                .hasSize(5)
                .containsExactly(id0, id1, id2, id3, id4);
    }

    @Test
    void 순서_업데이트() {
        long id0 = 0L;
        long id1 = 1L;
        long id2 = 2L;
        long id3 = 3L;
        long id4 = 4L;

        Sections sections = Sections.from(
                createSection(id1, id3, 10),
                createSection(id1, id2, 3),
                createSection(id0, id1),
                createSection(id3, id4));

        for (int i = 0; i < sections.size(); i++) {
            assertThat(sections.get(i).getOrderSeq()).isEqualTo(i);
        }
    }
}