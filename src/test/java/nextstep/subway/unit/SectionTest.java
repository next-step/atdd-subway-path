package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.junit.jupiter.api.Test;

import static nextstep.subway.fixture.LineFixture.createLine;
import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;


class SectionTest {

    @Test
    void 노선_추가_테스트() {
        //given
        Line line = createLine();
        Section section = createSection(0L, 1L);

        //when
        section.changeLine(line);

        //then
        assertThat(section.getLine()).isEqualTo(line);
        assertThat(line.getSectionsSize()).isEqualTo(1);
        assertThat(line.getLastSection()).isEqualTo(section);
    }

    @Test
    void 노선의_구간을_변경_한다() {
        //given
        Line oldLine = createLine();

        Line newLine = createLine();

        Section section1 = createSection(1L, 2L);
        Section section2 = createSection(2L, 3L);

        oldLine.addSection(section1);
        oldLine.addSection(section2);

        //when
        section2.changeLine(newLine);

        //then
        assertThat(oldLine.getSectionsSize()).isEqualTo(1);
        assertThat(oldLine.getLastSection()).isEqualTo(section1);
        assertThat(section2.getLine()).isEqualTo(newLine);
    }

    @Test
    void 구간에서_노선을_삭제한다() {
        //given
        Line line = createLine();

        Section section = createSection(1L, 2L);
        Section section2 = createSection(2L, 3L);
        line.addSection(section);
        line.addSection(section2);

        //when
        section2.removeLine();

        //then
        assertThat(line.getSectionsSize()).isEqualTo(1);
        assertThat(line.getLastSection()).isEqualTo(section);
        assertThat(section2.getLine()).isNull();
    }
}