package nextstep.subway.unit;


import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선도 테스트")
class LineTest {

    Line line;
    Station firstStation;
    Station secondStation;
    Section section;


    @BeforeEach
    void init() {
        line = new Line("신분당선", "red");
        firstStation = new Station("강남역");
        secondStation = new Station("판교역");
        section = new Section(line, firstStation, secondStation, 10);

        line.addSections(section);
    }

    @Test
    @DisplayName("지하철노선을 추가할 수 있다.")
    void addSection() {
        Section retriveSection = line.getSections().get(0);
        assertThat(retriveSection).isEqualTo(section);
    }

    @Test
    @DisplayName("추가하는 역의 상행역이 마지막 역의 하행역이 아니면 추가할 수 없다.")
    void addSectionFail() {
        Station lastStation = new Station("양재역");
        section = new Section(line, firstStation, lastStation, 10);
        assertThatThrownBy(() -> line.addSections(section))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.ENOUGH_ADD_CONNECT.toString());
    }

    @Test
    @DisplayName("지하철 노선을 조회할 수 있따.")
    void getStations() {
        List<Station> stations = line.getStations();

        assertThat(stations).hasSize(2);
        assertThat(stations).containsAnyOf(firstStation);
        assertThat(stations).containsAnyOf(secondStation);
    }

    @Test
    @DisplayName("지하철 노선의 구간은 1개 이상 유지되어야 한다.")
    void removeSectionFail() {
        assertThatThrownBy(() -> line.removeStation(secondStation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.ENOUGH_NOT_SECTION_SIZE.toString());
    }

    @Test
    @DisplayName("지하철 노선의 구간은 하행종점역이 아닌 역은 삭제할 수 없다.")
    void removeSectionFail2() {
        Station lastStation = new Station("청계산역");
        section = new Section(line, secondStation, lastStation, 10);
        line.addSections(section);

        assertThatThrownBy(() -> line.removeStation(secondStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ENOUGH_REMOVE_DOWN.toString());
    }

    @Test
    @DisplayName("지하철 노선의 하행종점역을 삭제할 수 있다.")
    void removeSection() {
        Station lastStation = new Station("청계산역");
        section = new Section(line, secondStation, lastStation, 10);
        line.addSections(section);

        line.removeStation(lastStation);

        List<Station> stations = line.getStations();
        assertThat(stations).hasSize(2);
        assertThat(stations).containsAnyOf(firstStation);
        assertThat(stations).containsAnyOf(secondStation);
        assertThat(stations).doesNotContain(lastStation);
    }
}
