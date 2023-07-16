package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

class LineTest {

    @Test
    void 노선_정보_수정() {
        // given
        Station upStation = createStation("강남역", 1L);
        Station downStation = createStation("언주역", 2L);

        Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);

        // when
        line.modify("3호선", "bg-green-400");

        // then
        Assertions.assertEquals("3호선", line.getName());
        Assertions.assertEquals("bg-green-400", line.getColor());
    }

    @Test
    void 구간_추가() {
        // given
        Station upStation = createStation("강남역", 1L);
        Station downStation = createStation("언주역", 2L);
        Station newSectionStation = createStation("성수역", 3L);

        Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
        Section section = Section.of(downStation, newSectionStation, 3L);

        // when
        Assertions.assertDoesNotThrow(() -> line.expandLine(section));

        // then
        Assertions.assertEquals(upStation, line.getUpStation());
        Assertions.assertEquals(newSectionStation, line.getDownStation());
        Assertions.assertEquals(13L, line.getDistance());
    }

    @Test
    void 모든_역_가져오기() {
        // given
        Station upStation = createStation("강남역", 1L);
        Station downStation = createStation("언주역", 2L);

        Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).hasSize(2)
                .containsExactly(upStation, downStation);
    }

    @Test
    void 구간_제거() {
        // given
        Station upStation = createStation("강남역", 1L);
        Station downStation = createStation("언주역", 2L);
        Station newSectionStation = createStation("성수역", 3L);

        Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
        Section section = Section.of(downStation, newSectionStation, 3L);
        line.expandLine(section);

        // when
        line.shorten(newSectionStation);

        // then
        Assertions.assertEquals(upStation, line.getUpStation());
        Assertions.assertEquals(downStation, line.getDownStation());
        Assertions.assertEquals(10L, line.getDistance());
    }

    private Line createLine(String name, String color, Long distance, Station upStation, Station downStation, Long id) {
        Line line = spy(Line.builder()
                .name(name)
                .color(color)
                .distance(distance)
                .upStation(upStation)
                .downStation(downStation)
                .build());
        given(upStation.getId()).willReturn(id);
        return line;
    }

    private Station createStation(String name, Long id) {
        Station station = spy(Station.create(() -> name));
        given(station.getId()).willReturn(id);
        return station;
    }
}
