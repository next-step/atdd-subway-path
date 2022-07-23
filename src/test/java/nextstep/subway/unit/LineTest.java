package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @DisplayName("구간 등록을 할 수 있다.")
    @Test
    void addSection() {
        // given
        final var upStation = new Station("선릉역");
        final var downStation = new Station("삼성역");
        final var line = new Line("2호선", "bg-green-600");
        final var section = new Section(line, upStation, downStation, 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections()).containsExactly(section);
    }

    @DisplayName("구간 등록 후 모든 역을 조회할 수 있다.")
    @Test
    void getStations() {
        // given
        final var upStation = new Station("선릉역");
        final var downStation = new Station("삼성역");
        final var line = new Line("2호선", "bg-green-600");
        final var section = new Section(line, upStation, downStation, 10);

        line.addSection(section);

        // when
        final var stations = line.findAllStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);

    }

    @DisplayName("하행 종착역 구간을 삭제할 수 있다.")
    @Test
    void removeSection() {
        // given
        final var firstStation = new Station("선릉역");
        final var secondStation = new Station("삼성역");
        final var thirdStation = new Station("종합운동장역");

        final var line = new Line("2호선", "bg-green-600");

        final var firstSection = new Section(line, firstStation, secondStation, 10);
        final var secondSection = new Section(line, secondStation, thirdStation, 5);

        line.addSection(firstSection);
        line.addSection(secondSection);

        // when
        line.removeSection(thirdStation);

        // then
        assertAll(
                () -> assertThat(line.findAllStations()).containsExactly(firstStation, secondStation),
                () -> assertThat(line.getSections()).hasSize(1)
        );

    }

    @DisplayName("구간 등록이 되어있지 않음에도 삭제를 시도하면 에러가 발생한다.")
    @Test
    void removeSectionExceptionWhenNoSectionExist() {
        // given
        final var station = new Station("삼성역");
        final var line = new Line("2호선", "bg-green-600");

        // when, then
        assertThatThrownBy(() -> line.removeSection(station))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("노선에 등록된 구간이 존재하지 않아 삭제할 수 없습니다.");

    }

    @DisplayName("삭제하고자 하는 구간이 상행 종착역과 하행 종착역만이 있다면 삭제 시 에러가 발생한다.")
    @Test
    void removeExceptionWhenOnlyOneSectionExist() {
        // given
        final var upStation = new Station("선릉역");
        final var downStation = new Station("삼성역");
        final var line = new Line("2호선", "bg-green-600");
        final var section = new Section(line, upStation, downStation, 10);

        line.addSection(section);

        // when, then
        assertThatThrownBy(() -> line.removeSection(downStation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("구간에 상행 종착역과 하행 종착역만 있기 때문에 삭제할 수 없습니다.");

    }

    @DisplayName("삭제하고자 하는 역이 하행 종착역이 아니면 에러가 발생한다.")
    @Test
    void removeExceptionWhenStationIsNotDownStation() {
        // given
        final var firstStation = new Station("선릉역");
        final var secondStation = new Station("삼성역");
        final var thirdStation = new Station("종합운동장역");

        final var line = new Line("2호선", "bg-green-600");

        final var firstSection = new Section(line, firstStation, secondStation, 10);
        final var secondSection = new Section(line, secondStation, thirdStation, 5);

        line.addSection(firstSection);
        line.addSection(secondSection);

        // when, then
        assertThatThrownBy(() -> line.removeSection(secondStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행 종착역만을 삭제할 수 있습니다.");

    }
}
