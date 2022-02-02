package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private static Stream<Arguments> provideLineStationsAndSection() {
        Line line = new Line();
        Station startingStation = new Station(1L, "강남역");
        Station endingStation = new Station(2L, "사당역");
        int distance = 10;
        Section section = new Section(line, startingStation, endingStation, distance);

        return Stream.of(Arguments.of(line, startingStation, endingStation, section));
    }

    @DisplayName("구간 추가")
    @ParameterizedTest
    @MethodSource("provideLineStationsAndSection")
    void add(Line line, Station startingStation, Station endingStation, Section section) {
        // given
        // when
        Sections sections = new Sections();
        sections.add(section);

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(startingStation, endingStation));
    }

    @DisplayName("역 목록 조회")
    @ParameterizedTest
    @MethodSource("provideLineStationsAndSection")
    void getStations(Line line, Station startingStation, Station endingStation, Section section) {
        // given
        // when
        Sections sections = new Sections(List.of(section));

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(startingStation, endingStation));
    }

    @DisplayName("구간 제거")
    @ParameterizedTest
    @MethodSource("provideLineStationsAndSection")
    void remove(Line line, Station startingStation, Station endingStation, Section section) {
        // given
        Station newEndingStation = new Station(3L, "역삼역");
        int distance = 10;
        Section newSection = new Section(line, endingStation, newEndingStation, distance);

        // when
        Sections sections = new Sections(new ArrayList<>(List.of(section, newSection)));
        sections.remove(newEndingStation);

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations).isEqualTo(List.of(startingStation, endingStation)),
                () -> assertThat(stations).doesNotContain(newEndingStation)
        );
    }

    @DisplayName("구간이 목록에서 마지막 역이 아닌 역 삭제 예외")
    @ParameterizedTest
    @MethodSource("provideLineStationsAndSection")
    void removeNonEndingSection(Line line, Station startingStation, Station endingStation, Section section) {
        // given
        Station newEndingStation = new Station(3L, "역삼역");
        int distance = 10;
        line.addSection(endingStation, newEndingStation, distance);

        // when
        // then
        assertThatIllegalArgumentException().isThrownBy(() -> line.removeSection(endingStation))
                .withMessage("구간이 목록에서 마지막 역이 아닙니다.");
    }
}
