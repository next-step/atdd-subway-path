package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CanNotAddSectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private Line secondaryLine;
    private Station gangnameStation;
    private Station eonjuStation;
    private Station seongsuStation;

    @BeforeEach
    void setUp() {
        gangnameStation = getStation("강남역", 1L);
        eonjuStation = getStation("언주역", 2L);
        seongsuStation = getStation("성수역", 3L);
        secondaryLine = getLine("2호선", "bg-red-600", 10L, gangnameStation, eonjuStation, 1L);
    }

    @Nested
    class 구간_추가 {

        @Test
        void 기존_노선_하행역에_신규_구간_하행역_등록_성공() {
            // given
            Section section = Section.of(eonjuStation, seongsuStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> secondaryLine.add(section));

            // then
            assertThat(secondaryLine.getStations()).map(Station::getName).containsExactly("강남역", "언주역", "성수역");
            Assertions.assertEquals(13L, secondaryLine.getDistance());
        }

        @Test
        void 기존_노선_상행역에_신규_구간_상행역_등록_성공() {
            // given
            Section section = Section.of(seongsuStation, gangnameStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> secondaryLine.add(section));

            // then
            assertThat(secondaryLine.getStations()).map(Station::getName).containsExactly("성수역", "강남역", "언주역");
            Assertions.assertEquals(13L, secondaryLine.getDistance());
        }

        @Test
        void 기존_노선_하행역에_신규_구간_상행역_등록_성공() {
            // given
            Section section = Section.of(seongsuStation, eonjuStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> secondaryLine.add(section));

            // then
            assertThat(secondaryLine.getStations()).map(Station::getName).containsExactly("강남역", "성수역", "언주역");
            Assertions.assertEquals(10L, secondaryLine.getDistance());
        }

        @Test
        void 기존_노선_상행역에_신규_구간_하행역_등록_성공() {
            // given
            Section section = Section.of(gangnameStation, seongsuStation, 3L);

            // when
            Assertions.assertDoesNotThrow(() -> secondaryLine.add(section));

            // then
            assertThat(secondaryLine.getStations()).map(Station::getName).containsExactly("강남역", "성수역", "언주역");
            Assertions.assertEquals(10L, secondaryLine.getDistance());
        }

        @Test
        void 신규_구간_하행역_기등록_실패() {
            // given
            Section section = Section.of(gangnameStation, eonjuStation, 3L);

            // when & then
            thenCode(() -> secondaryLine.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

        @CsvSource(value = {"10", "11", "12"})
        @ParameterizedTest
        void 신규_구간_길이가_기존과_동일하거나_더_크면_실패(long distance) {
            // given
            Section section = Section.of(gangnameStation, eonjuStation, distance);

            // when & then
            thenCode(() -> secondaryLine.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

        @Test
        void 신규_구간_기등록_실패() {
            // given
            Section section = Section.of(gangnameStation, eonjuStation, 10L);

            // when & then
            thenCode(() -> secondaryLine.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }

        @Test
        void 신규_구간_노선_구간_상행_하행_모두_미등록_실패() {
            // given
            Station newStation = getStation("뚝섬역", 4L);
            Section section = Section.of(seongsuStation, newStation, 10L);

            // when & then
            thenCode(() -> secondaryLine.add(section)).isInstanceOf(CanNotAddSectionException.class);
        }
    }

    @Test
    void 노선_정보_수정() {
        // when
        secondaryLine.modify("3호선", "bg-green-400");

        // then
        Assertions.assertEquals("3호선", secondaryLine.getName());
        Assertions.assertEquals("bg-green-400", secondaryLine.getColor());
    }


    @Test
    void 모든_역_가져오기() {
        // when
        List<Station> stations = secondaryLine.getStations();

        // then
        assertThat(stations).hasSize(2)
                .containsExactly(gangnameStation, eonjuStation);
    }

    @Nested
    class 구간_제거 {

        @Test
        void 상행역_구간_제거() {
            // given
            secondaryLine.add(Section.of(eonjuStation, seongsuStation, 3L));

            // when
            secondaryLine.remove(gangnameStation);

            // then
            List<Station> stations = secondaryLine.getStations();
            assertThat(stations).hasSize(2)
                    .containsExactly(eonjuStation, seongsuStation);
            Assertions.assertEquals(3L, secondaryLine.getDistance());
        }

        @Test
        void 하행역_구간_제거() {
            // given
            secondaryLine.add(Section.of(eonjuStation, seongsuStation, 3L));

            // when
            secondaryLine.remove(seongsuStation);

            // then
            List<Station> stations = secondaryLine.getStations();
            assertThat(stations).hasSize(2)
                    .containsExactly(gangnameStation, eonjuStation);
            Assertions.assertEquals(10L, secondaryLine.getDistance());
        }

        @Test
        void 중간역_구간_제거() {
            // given
            secondaryLine.add(Section.of(eonjuStation, seongsuStation, 3L));

            // when
            secondaryLine.remove(eonjuStation);

            // then
            List<Station> stations = secondaryLine.getStations();
            assertThat(stations).hasSize(2)
                    .containsExactly(gangnameStation, seongsuStation);
            Assertions.assertEquals(13L, secondaryLine.getDistance());
        }

        @Test
        void 없는_역을_제거_할수_없다() {
            // given
            Station notInLineStation = getStation("보라매역", 4L);
            secondaryLine.add(Section.of(eonjuStation, seongsuStation, 3L));

            // when & then
            thenCode(() -> secondaryLine.remove(notInLineStation)).isInstanceOf(Exception.class);

            // then
            List<Station> stations = secondaryLine.getStations();
            assertThat(stations).hasSize(3)
                    .containsExactly(gangnameStation, eonjuStation, seongsuStation);
            Assertions.assertEquals(13L, secondaryLine.getDistance());
        }

        @Test
        void 단일_구간_상행역_제거_불가() {
            // when & then
            thenCode(() -> secondaryLine.remove(gangnameStation)).isInstanceOf(Exception.class);

            // then
            List<Station> stations = secondaryLine.getStations();
            assertThat(stations).hasSize(2)
                    .containsExactly(gangnameStation, eonjuStation);
            Assertions.assertEquals(10L, secondaryLine.getDistance());
        }

        @Test
        void 단일_구간_하행역_제거_불가() {
            // when & then
            thenCode(() -> secondaryLine.remove(eonjuStation)).isInstanceOf(Exception.class);

            // then
            List<Station> stations = secondaryLine.getStations();
            assertThat(stations).hasSize(2)
                    .containsExactly(gangnameStation, eonjuStation);
            Assertions.assertEquals(10L, secondaryLine.getDistance());
        }
    }

    private static Line getLine(String name, String color, Long distance, Station upStation, Station downStation, Long id) {
        Line secondaryLine = spy(Line.builder()
                .name(name)
                .color(color)
                .distance(distance)
                .upStation(upStation)
                .downStation(downStation)
                .build());
        given(upStation.getId()).willReturn(id);
        return secondaryLine;
    }

    private static Station getStation(String name, Long id) {
        Station station = spy(Station.create(() -> name));
        given(station.getId()).willReturn(id);
        return station;
    }
}
