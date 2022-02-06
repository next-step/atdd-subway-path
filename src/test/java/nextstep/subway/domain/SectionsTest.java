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

    private static Stream<Arguments> provideArgumentsForVerifyingToRemove() {
        Line line = new Line();
        Station startingStation = new Station(1L, "강남역");
        Station middleStation = new Station(2L, "사당역");
        Station endingStation = new Station(3L, "신림역");

        int distance = 10;
        Section startingSection = new Section(line, startingStation, middleStation, distance);
        Section endingSection = new Section(line, middleStation, endingStation, distance);

        Sections sections = new Sections(new ArrayList<>(List.of(startingSection, endingSection)));

        return Stream.of(Arguments.of(sections, startingSection, endingSection));
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

    @DisplayName("기점 역 제거")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToRemove")
    void removeStartStation(Sections sections, Section startingSection, Section endingSection) {
        // given
        Station startingStation = startingSection.getUpStation();

        // when
        sections.remove(startingStation);

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations).isEqualTo(List.of(endingSection.getUpStation(), endingSection.getDownStation())),
                () -> assertThat(stations).doesNotContain(startingStation)
        );
    }

    @DisplayName("중간 역 제거")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToRemove")
    void removeMiddleStation(Sections sections, Section startingSection, Section endingSection) {
        // given
        Station middleStation = startingSection.getDownStation();

        // when
        sections.remove(middleStation);

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations).isEqualTo(List.of(startingSection.getUpStation(), endingSection.getDownStation())),
                () -> assertThat(stations).doesNotContain(middleStation)
        );
    }

    @DisplayName("종점 역 제거")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToRemove")
    void removeEndingStation(Sections sections, Section startingSection, Section endingSection) {
        // given
        Station endingStation = endingSection.getDownStation();

        // when
        sections.remove(endingStation);

        // then
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations).isEqualTo(List.of(startingSection.getUpStation(), startingSection.getDownStation())),
                () -> assertThat(stations).doesNotContain(endingStation)
        );
    }

    @DisplayName("구간이 한 개인 역 제거 예외")
    @ParameterizedTest
    @MethodSource("provideLineStationsAndSection")
    void removeASingleSection(Line line, Station startingStation, Station endingStation, Section section) {
        // given
        Sections sections = new Sections(List.of(section));

        // when
        // then
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> sections.remove(startingStation))
                        .withMessage("구간이 한 개면 삭제할 수 없습니다."),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> sections.remove(endingStation))
                        .withMessage("구간이 한 개면 삭제할 수 없습니다.")
        );
    }

    @DisplayName("구간에 존재 하지 않는 역 제거 예외")
    @ParameterizedTest
    @MethodSource("provideArgumentsForVerifyingToRemove")
    void removeAUnregisteredSection(Sections sections, Section startingSection, Section endingSection) {
        // given
        Station unregisteredStation = new Station(99L, "서울역");

        // when
        // then
        assertThatIllegalArgumentException().isThrownBy(() -> sections.remove(unregisteredStation))
                .withMessage("구간에 존재하지 않는 역입니다.");
    }
}
