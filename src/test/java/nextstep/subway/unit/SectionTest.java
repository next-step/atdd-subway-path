package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    private Line line;
    private Station station1;
    private Station station1_5;
    private Station station2;

    @BeforeEach
    void setUp() {
        line = new Line("line", "green");
        station1 = new Station("station1");
        station1_5 = new Station("station1.5");
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
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 구간의 상행역을 변경한다.")
    @Test
    void updateUpStation() {
        // given
        Section section = new Section(line, station1, station2, 5);

        // when
        section.updateUpStation(station1_5, 1);

        // then
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(station1_5),
            () -> assertThat(section.getDownStation()).isEqualTo(station2),
            () -> assertThat(section.getDistance()).isEqualTo(4)
        );
    }

    @DisplayName("기존 구간의 하행역을 변경한다.")
    @Test
    void updateDownStation() {
        // given
        Section section = new Section(line, station1, station2, 5);

        // when
        section.updateDownStation(station1_5, 1);

        // then
        assertAll(
            () -> assertThat(section.getUpStation()).isEqualTo(station1),
            () -> assertThat(section.getDownStation()).isEqualTo(station1_5),
            () -> assertThat(section.getDistance()).isEqualTo(4)
        );
    }

    @DisplayName("기존 구간의 길이를 증가시킨다.")
    @Test
    void increaseDistance() {
        // given
        Section section = new Section(line, station1, station2, 5);

        // when
        section.increaseDistance(5);

        // then
        assertThat(section.getDistance()).isEqualTo(10);
    }
}
