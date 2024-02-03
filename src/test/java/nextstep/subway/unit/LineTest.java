package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given
        final Station 강남역 = new Station(1L, "강남역");
        final Station 선릉역 = new Station(2L, "선릉역");
        final Line line = new Line(1L, "노선", "red", 강남역, 선릉역, 10);

        // when
        final Station 삼성역 = new Station(3L, "삼성역");
        final Section section = new Section(선릉역, 삼성역, 10, line);
        line.addSection(section);

        // then
        assertThat(line.getSections()).hasSize(2);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(line.getSections().get(1).getUpStation().getName()).isEqualTo("선릉역");
        assertThat(line.getSections().get(1).getDownStation().getName()).isEqualTo("삼성역");
    }

    @Test
    void getStations() {
        // given
        final Station 강남역 = new Station(1L, "강남역");
        final Station 선릉역 = new Station(2L, "선릉역");
        final Line line = new Line(1L, "노선", "red", 강남역, 선릉역, 10);

        // when
        final List<Section> sections = line.getSections();

        // then
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("선릉역");
    }

    @Test
    void removeSection() {
        // given
        final Station 강남역 = new Station(1L, "강남역");
        final Station 선릉역 = new Station(2L, "선릉역");
        final Line line = new Line(1L, "노선", "red", 강남역, 선릉역, 10);
        final Station 삼성역 = new Station(3L, "삼성역");
        final Section section = new Section(선릉역, 삼성역, 10, line);
        line.addSection(section);

        // when
        line.removeSection(삼성역.getId());

        // then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("선릉역");
    }
}
