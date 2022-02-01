package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class LineTest {

    private Station upStation;
    private Station downStation;
    private Line line;
    private Section section;

    @BeforeEach
    void setUp(){
        upStation = new Station("사당");
        downStation = new Station("방배");
        line = new Line("2호선","green");
        section = new Section(upStation, downStation, 5);
    }


    @DisplayName("등록된 구간이 없을 때 새로운 구간을 추가할 경우")
    @Test
    void section_should_be_added_to_line_when_there_is_no_section() {
        // when
        line.addSection(section);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
        assertThat(line.getSections()).containsExactly(section);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void section_should_be_added_to_line_when_there_is_section_that_is_able_to_connect() {
        // given
        line.addSection(section);
        Station newStation = new Station("서초");
        Section newSection = new Section(downStation, newStation, 3);

        // when
        line.addSection(newSection);

        // then
        assertThat(line.getSections()).contains(newSection);
    }

    @DisplayName("마지막 구간과 새로운 구간이 연결되지 못할 때 새로운 구간을 추가할 경우")
    @Test
    void add_section_should_throw_exception_when_there_is_section_that_is_not_able_to_connect() {
        // given
        line.addSection(section);
        Station newUpStation = new Station("서초");
        Station newDownStation = new Station("교대");
        Section newSection = new Section(newUpStation, newDownStation, 5);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> line.addSection(newSection)
        );
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void should_be_success_when_last_section_is_removable() {
        // given
        line.addSection(section);
        Station newStation = new Station("서초");
        Section newSection = new Section(downStation, newStation, 5);
        line.addSection(newSection);

        // when
        line.removeStation(newStation);

        // then
        assertThat(line.getStations()).doesNotContain(newStation);
    }

    @DisplayName("구간이 하나일 때 마지막 역 삭제 실패")
    @Test
    void should_be_fail_when_section_is_only_one() {
        // given
        line.addSection(section);

        // when & then
        assertThatIllegalArgumentException().isThrownBy(
                () -> line.removeStation(downStation)
        );
    }
}
