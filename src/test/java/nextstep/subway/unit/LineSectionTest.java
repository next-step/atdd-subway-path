package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineSectionTest {

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
         line = createLine();
         station1 = new Station("가양역");
         station2 = new Station("염창역");
         station3 = new Station("당산역");
         station4 = new Station("여의도역");
    }

    @Test
    @DisplayName("지하철 노선의 역 호출")
    void getStations() {
        //given
        addSection(line,station2,station3,10);
        addSection(line,station1,station2,10);
        addSection(line,station3,station4,10);

        //when
        List<Station> stations = line.getLineSection().getStations();

        //then
        assertThat(stations.get(0).getName()).isEqualTo("가양역");
        assertThat(stations.get(1).getName()).isEqualTo("염창역");
        assertThat(stations.get(2).getName()).isEqualTo("당산역");
        assertThat(stations.get(3).getName()).isEqualTo("여의도역");
    }

    private void addSection(Line line, Station station1, Station station2, int distance) {
        Section section1 = new Section(line, station1, station2, distance);
        line.getLineSection().add(section1);
    }

    @Test
    void add() {
        //when
        addSection(line,station2,station3,10);
        addSection(line,station1,station2,10);

        //then
        List<Station> stations = line.getLineSection().getStations();
        assertThat(stations.get(0).getName()).isEqualTo("가양역");
        assertThat(stations.get(1).getName()).isEqualTo("염창역");
        assertThat(stations.get(2).getName()).isEqualTo("당산역");
    }

    @Test
    void remove() {
    }

    @Test
    void removeLast() {
    }

    @Test
    void getSections() {
        //given
        addSection(line,station2,station3,10);
        addSection(line,station1,station2,10);
        addSection(line,station3,station4,10);

        //when
        List<Section> sections = line.getLineSection().getSections();

        //then
        assertThat(sections).hasSize(3);
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("가양역");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("염창역");
        assertThat(sections.get(1).getUpStation().getName()).isEqualTo("염창역");
        assertThat(sections.get(1).getDownStation().getName()).isEqualTo("당산역");
        assertThat(sections.get(2).getUpStation().getName()).isEqualTo("당산역");
        assertThat(sections.get(2).getDownStation().getName()).isEqualTo("여의도역");
    }

    @Test
    void size() {
        //given
        addSection(line,station2,station3,10);
        addSection(line,station1,station2,10);
        addSection(line,station3,station4,10);

        //when
        int size = line.getLineSection().size();

        //then
        assertThat(size).isEqualTo(3);
    }

    @Test
    void isEmpty() {
        assertThat(line.getLineSection().isEmpty()).isTrue();
    }

    @Test
    void checkArgument() {
    }


    private Line createLine() {
        return new Line("9호선","YELLOW");
    }
}
