package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line line;
    Station firstStation;
    Station secondStation;
    Section section;


    @BeforeEach
    void init() {
        Line line = new Line("신분당선", "red");
        Station firstStation = new Station("강남역");
        Station secondStation = new Station("판교역");
        Section section = new Section(line, firstStation, secondStation, 10);

        line.addSections(section);
    }
    @Test
    @DisplayName("지하철노선을 추가할 수 있다.")
    void addSection() {
        Section retriveSection =  line.getSections().get(0);
        assertThat(retriveSection).isEqualTo(section);
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
    void removeSection() {

    }
}
