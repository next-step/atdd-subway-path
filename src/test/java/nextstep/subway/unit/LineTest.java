package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LineTest {
    
    @Test
    @DisplayName("section을 추가할 수 있다.")
    void addSection() {
        Line line = new Line("test1", "red");
        Station firstStation = new Station();
        Station secondStation = new Station();
        Section section = new Section(line, firstStation, secondStation, 10);
        stationService.create
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
