package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 목록 관련 기능")
class SectionsTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Line line;

    @BeforeEach
    void setUp() {
        this.강남역 = new Station("강남역");
        this.역삼역 = new Station("역삼역");
        this.선릉역 = new Station("선릉역");
        this.line = new Line("2호선", "bg-green-500");
    }

    @DisplayName("구간 목록 조회")
    @Test
    void getSections() {
        Sections sections = new Sections();
        Section section = new Section(line, 강남역, 역삼역, 10);
        sections.add(section);

        List<Section> sectionList = sections.getSections();

        assertThat(sectionList).containsExactly(section);
    }

    @DisplayName("구간 목록에 구간을 추가한다.")
    @Test
    void add() {
        Sections sections = new Sections();
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 5);

        sections.add(section1);
        sections.add(section2);

        assertThat(sections.getSections()).containsExactly(section1, section2);
    }

    @DisplayName("구간 목록의 역 목록을 조회한다.")
    @Test
    void getStations() {
        Sections sections = new Sections();
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 5);
        sections.add(section1);
        sections.add(section2);

        List<Station> stations = sections.getStations();
        
        assertThat(stations).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("구간 목록의 구간을 제거한다.")
    @Test
    void remove() {
        Sections sections = new Sections();
        Section expected = new Section(line, 강남역, 역삼역, 10);
        sections.add(expected);
        sections.add(new Section(line, 역삼역, 선릉역, 5));

        sections.remove(선릉역);

        assertThat(sections.getSections()).containsExactly(expected);
    }
}
