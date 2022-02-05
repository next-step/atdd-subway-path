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

    private static Stream<Arguments> provideArgumentsForVerifyingToAdd() {
        Sections sections = new Sections();
        Line line = new Line();
        Station startingStation = new Station(1L, "사당역");
        Station endingStation = new Station(2L, "강남역");
        int distance = 10;
        sections.add(new Section(line, startingStation, endingStation, distance));

        return Stream.of(Arguments.of(sections, line, startingStation, endingStation, distance));
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

    @DisplayName("노선 기점 역을 신규 구간의 하행 역으로 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithNewStartingStation(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newStartingStation = new Station(3L, "역삼역");

        // when
        sections.add(new Section(line, newStartingStation, startingStation, distance));

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(newStartingStation, startingStation, endingStation));
    }

    @DisplayName("노선 종점 역을 신규 구간의 상행 역으로 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithNewEndingStation(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newEndingStation = new Station(3L, "역삼역");

        // when
        sections.add(new Section(line, endingStation, newEndingStation, distance));

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(startingStation, endingStation, newEndingStation));
    }

    @DisplayName("노선 구간에 추가된 역을 상행 역으로 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithNewDownStation(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newDownStation = new Station(3L, "역삼역");
        int newDistance = distance - 1;

        // when
        sections.add(new Section(line, startingStation, newDownStation, newDistance));

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(startingStation, newDownStation, endingStation));
    }

    @DisplayName("노선 구간에 추가된 역을 상행 역으로 기존 거리 이상 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithNewDownStationButDistanceIsOver(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newDownStation = new Station(3L, "역삼역");

        // when
        Section newSection = new Section(line, startingStation, newDownStation, distance);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(newSection))
                .withMessage("기존 구간 거리 이상 구간은 사이에 추가할 수 없습니다.");
    }

    @DisplayName("노선 구간에 추가된 역을 하행 역으로 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithNewUpStation(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newUpStation = new Station(3L, "역삼역");
        int newDistance = distance - 1;

        // when
        sections.add(new Section(line, newUpStation, endingStation, newDistance));

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(startingStation, newUpStation, endingStation));
    }

    @DisplayName("노선 구간에 추가된 역을 하행 역으로 기존 거리 이상 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithNewUpStationButDistanceIsOver(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newUpStation = new Station(3L, "역삼역");

        // when
        Section newSection = new Section(line, newUpStation, endingStation, distance);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(newSection))
                .withMessage("기존 구간 거리 이상 구간은 사이에 추가할 수 없습니다.");
    }

    @DisplayName("노선 구간에 추가된 역을 상행, 하행 역으로 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithDuplicatedStations(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        // when
        Section newSection = new Section(line, startingStation, endingStation, distance);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(newSection))
                .withMessage("기존 구간에 속하는 역 만으로 구간의 상행, 하행 역을 지정할 수 없습니다.");
    }

    @DisplayName("노선 구간에 추가 되지 않은 역을 상행, 하행 역으로 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithAllNewStations(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newUpStation = new Station(3L, "역삼역");
        Station newDownStation = new Station(4L, "잠실역");

        // when
        Section newSection = new Section(line, newUpStation, newDownStation, distance);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(newSection))
                .withMessage("기존 구간에 속하지 않는 역 만으로 구간의 상행, 하행 역을 지정할 수 없습니다.");
    }

    @DisplayName("복수 구간 추가")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToAdd")
    void addWithMultipleStations(Sections sections, Line line, Station startingStation, Station endingStation, int distance) {
        // given
        Station newStartingStation = new Station(3L, "홍대입구역");
        Station newUpStation = new Station(4L, "합정역");
        Station newDownStation = new Station(5L, "신도림역");
        Station newEndingStation = new Station(6L, "잠실역");
        Station newNewEndingStation = new Station(7L, "왕십리역");

        // when
        sections.add(new Section(line, newStartingStation, startingStation, distance));
        sections.add(new Section(line, newStartingStation, newDownStation, distance - 1));
        sections.add(new Section(line, newUpStation, newDownStation, distance - 2));
        sections.add(new Section(line, endingStation, newNewEndingStation, distance));
        sections.add(new Section(line, newEndingStation, newNewEndingStation, distance - 1));

        // then
        assertThat(sections.getStations()).isEqualTo(List.of(
                newStartingStation, newUpStation, newDownStation, startingStation, endingStation, newEndingStation, newNewEndingStation
        ));
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
