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
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(2L, "발산역");
        Line line = new Line("5호선", "purple");

        //When
        Section section = new Section(line, magok, balsan, 10);
        line.addSection(section);

        //Then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections()).containsExactly(section);
    }

    @Test
    void getStations() {
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(2L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section = new Section(line, magok, balsan, 10);
        line.addSection(section);

        //When
        List<Station> stations = line.getStations();

        //Then
        assertThat(stations).contains(magok, balsan);
    }

    @Test
    void removeSection() {
        //Given
        Station songjeong = new Station(1L, "송정역");
        Station magok = new Station(2L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section1 = new Section(line, songjeong, magok, 7);
        Section section2 = new Section(line, magok, balsan, 10);
        line.addSection(section1);
        line.addSection(section2);

        //When
        line.removeSection(line.getSections().size()-1);

        //Then
        assertThat(line.getSections()).hasSize(1);

    }
}
