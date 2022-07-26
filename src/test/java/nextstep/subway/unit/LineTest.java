package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;
    private Station upStation;
    private Station downStation;
    private Section section;

    @BeforeEach
    void init() {
        line = new Line("신분당선", "빨강");
        upStation = new Station("강남역");
        downStation = new Station("양재역");
        section = new Section(line, upStation, downStation, 10);
    }

    @DisplayName("")
    @Test
    void addSection() {
        // given line 에 추가할 section 이 존재하고
        Station newUpStation = new Station("양재역");
        Station newDownStation = new Station("양재시민의숲");
        Section newSection = new Section(line, newUpStation, newDownStation, 8);

        // when line 에 addSection(section) 을 진행하면
        line.addSection(newSection);

        // then line.sections 에 section 이 추가된다.
        assertThat(line.getSections()).contains(newSection);
    }

    @DisplayName("")
    @Test
    void getStations() {
        // given line 에 추가할 section 이 존재하고
        line.addSection(section);

        Station newUpStation = new Station("양재역");
        Station newDownStation = new Station("양재시민의숲");
        Section newSection = new Section(line, newUpStation, newDownStation, 8);

        // when line 에 section 을 추가한 후 station 조회(getStations())를 진행하면
        line.addSection(newSection);
        List<Station> stations = line.getStations();

        // then 추가한 section 에 포함된 두 개 역이 포함되어 있다.
        assertThat(stations).containsExactly(upStation, downStation, newDownStation);
    }

    @DisplayName("")
    @Test
    void removeSection() {
        // given line 과 두 개의 station, 하나의 section 이 존재하고(init) 이를 추가한 후
        line.addSection(section);

        Station newUpStation = new Station("양재역");
        Station newDownStation = new Station("양재시민의숲");
        Section newSection = new Section(line, newUpStation, newDownStation, 8);
        line.addSection(newSection);

        assertThat(line.getSections()).contains(newSection);

        // when line 에서 section 을 삭제하면
        line.removeSection(newSection);

        // then line 이 비어있게 된다.
        assertThat(line.getSections()).containsOnly(section);
    }
}
