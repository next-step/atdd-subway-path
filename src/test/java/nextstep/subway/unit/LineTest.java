package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.sections.CantDeleteLastSectionException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    Station upStation;
    Station downStation;
    Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "건대입구역");
        line = new Line(3L, "2호선", "green");
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

    @DisplayName("Line에 section이 없는데 section을 제거하려 한 경우 예외를 발생한다")
    @Test
    void remove_section_when_no_section() {
        //when
        ThrowableAssert.ThrowingCallable actual = () -> line.deleteSection(downStation);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(CantDeleteLastSectionException.class)
                .hasMessage("노선의 마지막 하나 남은 구간은 삭제할 수 없습니다");
    }
}
