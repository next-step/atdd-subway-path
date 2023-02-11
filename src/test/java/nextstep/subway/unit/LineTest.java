package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {

    @DisplayName("노선에 구간을 추가할 수 있다")
    @Test
    void addSection() {
        Line line = new Line("4호선", "#00A5DE");

        Station upStation = new Station("사당");
        Station downStation = new Station("금정");
        Section newSection = new Section(line, upStation, downStation, 10);

        Section addedSection = line.addSection(newSection);
        assertThat(addedSection).isEqualTo(newSection);
    }

    @DisplayName("노선의 모든 역을 조회할 수 있다")
    @Test
    void getStations() {
        Line line = new Line("4호선", "#00A5DE");

        Station upStation = new Station("사당");
        Station downStation = new Station("금정");
        Section newSection = new Section(line, upStation, downStation, 10);

        line.addSection(newSection);

        List<Station> stations = line.getStations();
        assertAll(
            () -> assertThat(stations.size()).isEqualTo(2),
            () -> assertThat(stations).containsOnly(upStation, downStation)
        );
    }

    @DisplayName("노선의 마지막 구간을 삭제할 수 있다")
    @Test
    void removeSection() {
        Line line = new Line("4호선", "#00A5DE");

        Station upStation = new Station("사당");
        Station downStation = new Station("금정");
        Station newDownStation = new Station("오이도");

        Section newSection = new Section(line, upStation, downStation, 10);
        Section newSection2 = new Section(line, downStation, newDownStation, 13);

        line.addSection(newSection);
        line.addSection(newSection2);

        line.removeSection(newDownStation);
        List<Station> stations = line.getStations();
        assertAll(
            () -> assertThat(stations.size()).isEqualTo(2),
            () -> assertThat(stations).containsOnly(upStation, downStation)
        );
    }
}
