package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("새로운 구간을 정상적으로 추가했다")
    @Test
    void addSection() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Station newStation = new Station("선정릉역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when
        line.addSection(new Section(line, downStation, newStation, 6));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("노선의 역 목록이 정상 조회되었습니다.")
    @Test
    void getStations() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("구간에서 하행 종점역이 정상적으로 삭제되었습니다.")
    @Test
    void removeSection() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Station newStation = new Station("선정릉역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));
        line.addSection(new Section(line, downStation, newStation, 6));

        // when
        line.deleteSection(newStation);

        // then
        assertAll(
                () -> assertThat(line.getStations()).containsExactly(upStation, downStation),
                () -> assertThat(line.getStations()).doesNotContain(newStation)
        );
    }

    @DisplayName("신규 구간을 추가하는 경우 상행역이 이전 하행종점역과 일치하지 않으면 오류가 발생한다")
    @Test
    void addSectionNotMatchedEndStationException() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Station newStation = new Station("선정릉역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when & then
        assertThatThrownBy(() -> line.addSection(new Section(line, upStation, newStation, 6)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("신규 구간을 추가하는 경우 신규역이 이전에 등록된 역 목록에 있다면 오류가 발생한다")
    @Test
    void addSectionMatchedPreviouslyStationException() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Station newStation = new Station("신논현역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when & then
        assertThatThrownBy(() -> line.addSection(new Section(line, downStation, newStation, 6)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간이 한개만 존재하는 경우 구간 삭제를 하면 오류가 발생한다")
    @Test
    void removeSectionIsOneSectionException() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when & then
        assertThatThrownBy(() -> line.deleteSection(downStation))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @DisplayName("노선에서 삭제를 하는 역이 하행 종점역이 아닌경우 오류가 발생한다")
    @Test
    void removeSectionNotMatchedEndStationException() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Station newStation = new Station("선정릉역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));
        line.addSection(new Section(line, downStation, newStation, 6));
        
        // when & then
        assertThatThrownBy(() -> line.deleteSection(upStation))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
