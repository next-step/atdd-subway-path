package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given line 과 section 이 존재하고
        Line line = new Line("신분당선", "빨강");
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        Section section = new Section(line, upStation, downStation, 10);

        // when line 에 addSection(section) 을 진행하면
        line.addSection(section);

        // then line.sections 에 section 이 추가된다.
        assertThat(line.getSections()).contains(section);
    }

    @Test
    void getStations() {
        // given line 과 두 개의 station, 하나의 section 이 존재하고
        Line line = new Line("신분당선", "빨강");
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        Section section = new Section(line, upStation, downStation, 10);

        // when line 에 section 을 추가한 후 station 조회(getStations())를 진행하면
        line.addSection(section);
        List<Station> stations = line.getStations();

        // then 추가한 section 에 포함된 두 개 역이 포함되어 있다.
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @Test
    void removeSection() {
        // given line 과 두 개의 station, 하나의 section 이 존재하고 이를 추가한 후
        Line line = new Line("신분당선", "빨강");
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        Section section = new Section(line, upStation, downStation, 10);
        line.addSection(section);
        List<Station> stations = line.getStations();
        assertThat(stations).containsExactly(upStation, downStation);

        // when line 에서 section 을 삭제하면
        line.removeSection(section);

        // then line 이 비어있게 된다.
        assertThat(line.getSections()).isEmpty();
    }
}
