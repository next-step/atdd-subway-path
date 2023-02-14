package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("2호선", "green", 강남역, 정자역, 10);
        line.addSection(정자역, 광교역, 15);
        List<Section> sections = line.getSections();
        assertThat(sections.get(1).getDownStation()).isEqualTo(광교역);
    }

    @Test
    void getStations() {
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("2호선", "green", 강남역, 정자역, 10);
        line.addSection(정자역, 광교역, 15);
        List<Station> stations = line.getStations();
        assertThat(stations).containsExactly(Arrays.array(강남역, 정자역, 광교역));
    }

    @Test
    void removeSection() {
        Station 강남역 = new Station("강남역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        Line line = new Line("2호선", "green", 강남역, 정자역, 10);
        line.addSection(정자역, 광교역, 15);
        line.removeSection(광교역);
        List<Station> stations = line.getStations();
        assertThat(stations).doesNotContain(광교역);
    }
}
