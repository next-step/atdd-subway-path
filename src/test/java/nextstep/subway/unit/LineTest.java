package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("노선 추가")
    @Test
    void addSection() {
        var sut = new Line();
        var upStation = new Station("광교역");
        var downStation = new Station("광교중앙역");
        var distance = 10;

        sut.addSection(upStation, downStation, distance);

        assertAll(
                () -> assertThat(sut.getSections()).hasSize(1),
                () -> assertThat(sut.getSections().get(0).getLine()).isEqualTo(sut),
                () -> assertThat(sut.getSections().get(0).getUpStation()).isEqualTo(upStation),
                () -> assertThat(sut.getSections().get(0).getDownStation()).isEqualTo(downStation),
                () -> assertThat(sut.getSections().get(0).getDistance()).isEqualTo(distance)
        );
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
