package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given
        final Station 강남역 = new Station(1L, "강남역");
        final Station 선릉역 = new Station(2L, "선릉역");
        final Station 삼성역 = new Station(3L, "삼성역");
        final Line line = new Line(1L, "노선", "red", 강남역, 선릉역, 10);

        // when
        line.addSection(선릉역, 삼성역, 10);

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 선릉역, 삼성역));
    }

    @Test
    void getStations() {
        // given
        final Station 강남역 = new Station(1L, "강남역");
        final Station 선릉역 = new Station(2L, "선릉역");
        final Line line = new Line(1L, "노선", "red", 강남역, 선릉역, 10);

        // when
        final List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactlyElementsOf(Arrays.asList(강남역, 선릉역));
    }

    @Test
    void removeSection() {
        // given
        final Station 강남역 = new Station(1L, "강남역");
        final Station 선릉역 = new Station(2L, "선릉역");
        final Line line = new Line(1L, "노선", "red", 강남역, 선릉역, 10);
        final Station 삼성역 = new Station(3L, "삼성역");
        line.addSection(선릉역, 삼성역, 10);

        // when
        line.removeSection(삼성역.getId());

        // then
        assertThat(line.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 선릉역));
    }
}
