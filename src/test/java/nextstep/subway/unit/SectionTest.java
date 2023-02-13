package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.IdenticalSectionAlreadyExistsInLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    private Line line;
    private Station station1;
    private Station station2;

    @BeforeEach
    void setUp() {
        line = new Line("line", "green");
        station1 = new Station("station1");
        station2 = new Station("station2");
    }

    @DisplayName("지하철 구간이 특정 역을 상행역으로 갖는지 확인한다.")
    @Test
    void hasUpStation() {
        // given
        Section section = new Section(line, station1, station2, 5);

        // when & then
        assertAll(
            () -> assertThat(section.hasUpStation(station1)).isTrue(),
            () -> assertThat(section.hasUpStation(station2)).isFalse()
        );
    }

    @DisplayName("지하철 구간이 특정 역을 하행역으로 갖는지 확인한다.")
    @Test
    void hasDownStation() {
        // given
        Section section = new Section(line, station1, station2, 5);

        // when & then
        assertAll(
            () -> assertThat(section.hasDownStation(station1)).isFalse(),
            () -> assertThat(section.hasDownStation(station2)).isTrue()
        );
    }

    @DisplayName("지하철 구간 등록 시, 상행역과 하행역이 같으면 예외가 발생한다.")
    @Test
    void cannotCreateSectionWithIdenticalStations() {
        // when & then
        assertThatThrownBy(() -> new Section(line, station1, station1, 5))
            .isInstanceOf(IdenticalSectionAlreadyExistsInLineException.class);
    }
}
