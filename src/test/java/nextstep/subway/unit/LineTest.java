package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @DisplayName("노선 생성후 첫 구간 추가하기")
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

    @DisplayName("노선 중간에 구간 추가하기")
    @Test
    void addSectionBaseOnUpstation() {
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");

        //When
        Section section1 = new Section(line, 1L, magok, woojangsan, 10);
        line.addSection(section1);

        Section section2 = new Section(line, 2L, magok, balsan, 3);
        line.addSection(section2);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sections.get(0).getDownStation()).isEqualTo(section2.getDownStation());
        assertThat(sections.get(1).getUpStation()).isEqualTo(section2.getDownStation());
        assertThat(sections.get(1).getDownStation()).isEqualTo(section1.getDownStation());
    }

    @DisplayName("노선 처음에 구간 추가하기")
    @Test
    void addSectionFirst(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, 1L, balsan, woojangsan, 10);
        line.addSection(section1);

        Section section2 = new Section(line, 2L, magok, balsan, 3);
        line.addSection(section2);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sections.get(0).getDownStation()).isEqualTo(section2.getDownStation());
        assertThat(sections.get(1).getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(sections.get(1).getDownStation()).isEqualTo(section1.getDownStation());
    }

    @DisplayName("노선 마지막에 구간 추가하기")
    @Test
    void addSectionEnd(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, balsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, balsan, woojangsan, 3);
        line.addSection(section2);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(sections.get(0).getDownStation()).isEqualTo(section1.getDownStation());
        assertThat(sections.get(1).getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sections.get(1).getDownStation()).isEqualTo(section2.getDownStation());
    }

    @DisplayName("상행과 하행이 없는 구간 추가할때 예외 발생")
    @Test
    void hasNoStationException(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Station woojangsan = new Station(4L, "우장산역");
        Station gimpo = new Station(4L, "김포공항역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, balsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, woojangsan, gimpo, 2);
        assertThatThrownBy(() ->line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 등록된 구간을 추가할때 예외 발생")
    @Test
    void alreadyRegistException() {
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, 1L, magok, balsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, 2L, magok, balsan, 2);
        assertThatThrownBy(() -> line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 구간보다 긴 구간 추가시 예외 발생")
    @Test
    void distanceException(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, woojangsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, balsan, woojangsan, 10);
        assertThatThrownBy(() ->line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @DisplayName("노선의 구간 가져오기")
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
        assertThat(stations).containsExactly(magok, balsan);
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void removeSection() {
        //Given
        Station songjeong = new Station(1L, "송정역");
        Station magok = new Station(2L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section1 = new Section(line, 1L, songjeong, magok, 7);
        Section section2 = new Section(line, 2L, magok, balsan, 10);
        line.addSection(section1);
        line.addSection(section2);

        //When
        line.removeSection(magok);

        //Then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getStations()).containsExactly(songjeong, balsan);
    }

    @DisplayName("한개만 있는 구간 삭제시 예외 발생")
    @Test
    void removeOnlyOneSectionLineException() {
        //Given
        Station songjeong = new Station(1L, "송정역");
        Station magok = new Station(2L, "마곡역");
        Line line = new Line("5호선", "purple");
        Section section1 = new Section(line, 1L, songjeong, magok, 7);
        line.addSection(section1);


        //When Then
        assertThatThrownBy(() -> line.removeSection(magok))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선에 없는 역을 삭제 요청시 예외 발생")
    @Test
    void removeDoNotHaveStationInLineException() {
        //Given
        Station songjeong = new Station(1L, "송정역");
        Station magok = new Station(2L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section1 = new Section(line, 1L, songjeong, magok, 7);
        line.addSection(section1);

        //When Then
        assertThatThrownBy(() -> line.removeSection(balsan))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
