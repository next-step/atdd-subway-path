package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CanNotAddSectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

class LineTest {

    @Nested
    class 구간_추가 {

        @Test
        void 기존_노선_하행역에_신규_구간_하행역_등록_성공() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);
            Station newSectionStation = createStation("성수역", 3L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(downStation, newSectionStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> line.add(section));

            // then
            assertThat(line.getStations()).map(Station::getName).containsExactly("강남역", "언주역", "성수역");
            Assertions.assertEquals(13L, line.getDistance());
        }

        @Test
        void 기존_노선_상행역에_신규_구간_상행역_등록_성공() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);
            Station newSectionStation = createStation("성수역", 3L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(newSectionStation, upStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> line.add(section));

            // then
            assertThat(line.getStations()).map(Station::getName).containsExactly("성수역", "강남역", "언주역");
            Assertions.assertEquals(13L, line.getDistance());
        }

        @Test
        void 기존_노선_하행역에_신규_구간_상행역_등록_성공() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);
            Station newSectionStation = createStation("성수역", 3L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(newSectionStation, downStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> line.add(section));

            // then
            assertThat(line.getStations()).map(Station::getName).containsExactly("강남역", "성수역", "언주역");
            Assertions.assertEquals(10L, line.getDistance());
        }

        @Test
        void 기존_노선_상행역에_신규_구간_하행역_등록_성공() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);
            Station newSectionStation = createStation("성수역", 3L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(upStation, newSectionStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> line.add(section));

            // then
            assertThat(line.getStations()).map(Station::getName).containsExactly("강남역", "성수역", "언주역");
            Assertions.assertEquals(10L, line.getDistance());
        }

        @Test
        void 신규_구간_하행역_기등록_실패() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(upStation, downStation, 3L);

            // when & then
            thenCode(() -> line.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

        @CsvSource(value = {"10", "11", "12"})
        @ParameterizedTest
        void 신규_구간_길이가_기존과_동일하거나_더_크면_실패(long distance) {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(upStation, downStation, distance);

            // when & then
            thenCode(() -> line.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

        @Test
        void 신규_구간_기등록_실패() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(downStation, downStation, 10L);

            // when & then
            thenCode(() -> line.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

        @Test
        void 신규_구간_노선_구간_상행_하행_모두_미등록_실패() {
            // given
            Station upStation = createStation("강남역", 1L);
            Station downStation = createStation("언주역", 2L);
            Station newUpStation = createStation("성수역", 3L);
            Station newDownStation = createStation("뚝섬역", 4L);

            Line line = createLine("2호선", "bg-red-600", 10L, upStation, downStation, 1L);
            Section section = Section.of(newUpStation, newDownStation, 10L);

            // when & then
            thenCode(() -> line.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

    }


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
        line.add(Section.of(downStation, newSectionStation, 3L));

        // when
        line.remove(newSectionStation);

        // then
        Assertions.assertEquals(upStation, line.getStartOfLine());
        Assertions.assertEquals(downStation, line.getEndOfLine());
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
