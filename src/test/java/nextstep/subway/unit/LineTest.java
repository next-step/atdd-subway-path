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

    @DisplayName("노선 내 역 조회")
    @Test
    void getStations() {
        var 광교역 = new Station("광교역");
        var 광교중앙역 = new Station("광교중앙역");
        var 상현역 = new Station("상현역");
        var sut = new Line();
        sut.addSection(광교역, 광교중앙역, 10);
        sut.addSection(광교중앙역, 상현역, 10);

        var stations = sut.getStations();

        assertThat(stations).containsExactly(광교역, 광교중앙역, 상현역);
    }

    @Test
    void removeSection() {
    }
}
