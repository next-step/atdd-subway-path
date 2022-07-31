package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static nextstep.subway.utils.TestVariables.*;

import java.util.List;
import nextstep.subway.applicaion.dto.SectionRequest;
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
    private Station station5;

    @BeforeEach
    void setUp() {
         line = createLine();
         station1 = new Station(1L,가양역);
         station2 = new Station(2L,염창역);
         station3 = new Station(3L,당산역);
         station4 = new Station(4L,여의도역);
         station5 = new Station(5L,동작역);
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
        assertThat(stations.get(0).getName()).isEqualTo(가양역);
        assertThat(stations.get(1).getName()).isEqualTo(염창역);
        assertThat(stations.get(2).getName()).isEqualTo(당산역);
        assertThat(stations.get(3).getName()).isEqualTo(여의도역);
    }

    private void addSection(Line line, Station station1, Station station2, int distance) {
        Section section1 = new Section(line, station1, station2, distance);
        line.getLineSection().add(section1);
    }

    @Test
    @DisplayName("구간 추가 정상 동작")
    void add() {
        //when
        addSection(line,station2,station5,40);
        addSection(line,station1,station2,10);
        addSection(line,station2,station3,10);
        addSection(line,station4,station5,10);

        //then
        List<Station> stations = line.getLineSection().getStations();
        assertThat(stations.get(0).getName()).isEqualTo(가양역);
        assertThat(stations.get(1).getName()).isEqualTo(염창역);
        assertThat(stations.get(2).getName()).isEqualTo(당산역);
        assertThat(stations.get(3).getName()).isEqualTo(여의도역);
        assertThat(stations.get(4).getName()).isEqualTo(동작역);

    }

    @Test
    @DisplayName("정렬된 구간 호출 정상 동작")
    void getSections() {
        //given
        addSection(line,station2,station3,10);
        addSection(line,station1,station2,10);
        addSection(line,station3,station4,10);

        //when
        List<Section> sections = line.getLineSection().getSections();

        //then
        assertThat(sections).hasSize(3);
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo(가양역);
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo(염창역);
        assertThat(sections.get(1).getUpStation().getName()).isEqualTo(염창역);
        assertThat(sections.get(1).getDownStation().getName()).isEqualTo(당산역);
        assertThat(sections.get(2).getUpStation().getName()).isEqualTo(당산역);
        assertThat(sections.get(2).getDownStation().getName()).isEqualTo(여의도역);
    }

    @Test
    @DisplayName("구간 의 사이즈 호출 정상 동작")
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
    @DisplayName("구간 empty 여부 정상 동작")
    void isEmpty() {
        assertThat(line.getLineSection().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("동일구간 존재확인 validation")
    void checkAddArgument1() {
        //given
        addSection(line,station1,station2,10);

        //when
        assertThatThrownBy(()->line.getLineSection().checkAddArgument(new SectionRequest(station1.getId(),station2.getId(),4)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("동일한 역의 구간이 존재합니다. upStationId="+station1.getId()+", downStationId="+station2.getId());
    }

    @Test
    @DisplayName("어느곳과도 연결되지 않는 구간 추가 validation")
    void checkAddArgument2() {
        //given
        addSection(line,station1,station2,10);

        //when
        assertThatThrownBy(()->line.getLineSection().checkAddArgument(new SectionRequest(station3.getId(),station4.getId(),10)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("연결되는 구간의 역이 없습니다. upStationId="+station3.getId() +", downStationId="+station4.getId());
    }

    @Test
    @DisplayName("구간 추가 길이 확인 validation")
    void checkAddArgument3() {
        //given
        addSection(line,station1,station2,10);

        //when
        assertThatThrownBy(()->line.getLineSection().checkAddArgument(new SectionRequest(station1.getId(),station2.getId(),10)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("기존 구간보다 작은 길이의 구간을 입력해주세요. distance="+10);
    }


    private Line createLine() {
        return new Line(구호선,YELLOW);
    }
}
