package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    Station upStation;
    Station downStation;
    Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("강남역");
        downStation = new Station("건대입구역");
        line = new Line("2호선", "green");
    }

    @DisplayName("비어있는 line에 신규 section을 추가한다")
    @Test
    void addSection() {
        // given

        // when
        Section newSection = new Section(line, upStation, downStation, 10);
        line.addSection(newSection);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("이미 section이 있는 line에 신규 section을 추가한다")
    @Test
    public void add_section_already_has_section() {
        // given
        line.addSection(new Section(line, upStation, downStation, 10));
        Station newStation = new Station("신규역");

        // when
        line.addSection(new Section(line, downStation, newStation, 10));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("Line에 포함된 모든 역을 조회한다")
    @Test
    void getStations() {
        // given
        line.addSection(new Section(line, upStation, downStation, 10));

        // when
        List<Station> stationList = line.getStations();

        // then
        assertThat(stationList).containsExactly(upStation, downStation);
    }

    @DisplayName("Line의 마지막 section을 제거한다")
    @Test
    void removeSection() {
        // given
        line.addSection(new Section(line, upStation, downStation, 10));

        // when
        line.deleteLastSection(downStation);

        // then
        assertThat(line.isEmptySections()).isTrue();
    }

    @DisplayName("Line에 section이 없는데 section을 제거하려 한 경우 예외를 발생한다")
    @Test
    void remove_section_when_no_section() {
        // given

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> line.deleteLastSection(downStation));

        // then
        assertThat(exception).isInstanceOf(IllegalStateException.class);
    }
}
