package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 단위 테스트")
class LineTest {

    private Station upStation = new Station("강남역");;
    private Station downStation = new Station("역삼역");;
    private Line line = new Line("2호선", "green", upStation, downStation);;
    private Section section = new Section(upStation, downStation, 10, line);;

    @BeforeEach
    void beforeEach(){
        line.getSections().clear();
    }


    @Test
    void addSection() {
        line.addSection(section);
        assertThat(line.contains(section)).isTrue();
    }

    @Test
    void getStations() {
        line.addSection(section);
        assertThat(line.contains(section)).isTrue();
    }

    @Test
    void removeSection() {
        line.addSection(section);
        line.removeSection(section);
        assertThat(line.contains(section)).isFalse();
    }

    @DisplayName("지하철 노선의 특정 역 앞에 역을 등록할 수 있다. (맨 앞)")
    @Test
    void addFirst() {
        Station newStation = new Station("교대역");
        line.addSection(section);
        line.addStationBefore(newStation, upStation, 2);
        assertThat(line.getStartStation()).isEqualTo(newStation);
    }

    @DisplayName("지하철 노선의 특정 역 앞에 역을 등록할 수 있다. (중간)")
    @Test
    void addBetween() {
        Station newStation = new Station("교대역");
        line.addSection(section);
        line.addStationBefore(newStation, downStation, 2);
        assertThat(line.getSections().size()).isEqualTo(2);
        assertThat(line.getSections().contains(newStation)).isTrue();
        assertThat(line.getStartStation()).isEqualTo(upStation);
        assertThat(line.getEndStation()).isEqualTo(downStation);
    }

    @DisplayName("노선에 등록된 역 제거 시 해당 역이 노선 가운데 있어도 제거할 수 있다.")
    @Test
    void removeStation() {
        Station newStation = new Station("교대역");
        line.addSection(section);
        line.addStationBefore(newStation, downStation, 2);
        line.removeStation(newStation);
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections().contains(newStation)).isFalse();
        assertThat(line.getStartStation()).isEqualTo(upStation);
        assertThat(line.getEndStation()).isEqualTo(downStation);
    }

    @DisplayName("노선에 등록된 역 제거 시 해당 역이 노선 가장 앞에 있을 때는 제거할 수 없다.")
    @Test
    void removeFirstStation() {
        Station newStation = new Station("교대역");
        line.addSection(section);
        line.addStationBefore(newStation, upStation, 2);
        line.removeStation(newStation);
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections().contains(newStation)).isFalse();
        assertThat(line.getStartStation()).isEqualTo(upStation);
        assertThat(line.getEndStation()).isEqualTo(downStation);

    }
}
