package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    @DisplayName("지하철노선을 추가할 수 있다.")
    void addSection() {
        Line line = new Line("신분당선", "red");
        Station firstStation = new Station("강남역");
        Station secondStation = new Station("판교역");
        Section section = new Section(line, firstStation, secondStation, 10);
        line.addSections(section);

        Section retriveSection =  line.getSections().get(0);
        assertThat(retriveSection).isEqualTo(section);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
