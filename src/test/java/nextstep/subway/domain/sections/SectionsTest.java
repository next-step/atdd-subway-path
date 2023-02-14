package nextstep.subway.domain.sections;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import nextstep.subway.unit.Fixtures;

class SectionsTest {
    Line line;
    Sections sections;

    @BeforeEach
    void setup() {
        line = new Line("새로운노선", "새로운색깔");
        sections = new Sections();

        sections.addSection(Fixtures.createSection(3L, line, Fixtures.정자역, Fixtures.미금역, 10), line);
        sections.addSection(Fixtures.createSection(2L, line, Fixtures.판교역, Fixtures.정자역, 10), line);
    }

    @Test
    @DisplayName("비어있는 노선에 구간을 추가한다.")
    void addSectionWhenEmpty() {
        // when
        Sections emptySections = new Sections();
        emptySections.addSection(Fixtures.createSection(1L, line, Fixtures.판교역, Fixtures.정자역, 10), line);
        List<Station> stations = emptySections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(2),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역)
        );
    }

    @Test
    @DisplayName("최상단구간에 구간을 추가한다.")
    void addUpmostSection() {
        sections.addSection(Fixtures.createSection(1L, line, Fixtures.양재역, Fixtures.판교역, 10), line);
        List<Station> stations = sections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(4),
            () -> assertThat(stations).containsExactly(Fixtures.양재역, Fixtures.판교역, Fixtures.정자역, Fixtures.미금역)
        );
    }

    @Test
    @DisplayName("최하단구간에 구간을 추가한다.")
    void addDownmostSection() {
        sections.addSection(Fixtures.createSection(1L, line, Fixtures.미금역, Fixtures.광교역, 10), line);
        List<Station> stations = sections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(4),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역, Fixtures.미금역, Fixtures.광교역)
        );
    }

    @Test
    @DisplayName("기존 구간 사이에 구간을 추가한다.")
    void addMiddleSection() {
        sections.addSection(Fixtures.createSection(1L, line, Fixtures.정자역, Fixtures.광교역, 5), line);
        List<Station> stations = sections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(4),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역, Fixtures.광교역, Fixtures.미금역)
        );
    }

    @ParameterizedTest(name = "구간에 존재하는 역으로 구간 추가시 예외를 던진다; {2}")
    @MethodSource("provideInvalidSectionSource")
    void addSectionWithExistingStation(Section invalidSection, Line line, String message) {
        assertThatThrownBy(() -> sections.addSection(invalidSection, line))
            .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    @DisplayName("노선의 마지막 역이 포함된 구간을 제거한다.")
    void deleteSection() {
        // when
        sections.deleteSection(Fixtures.미금역.getId(), line);
        List<Station> stations = sections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(2),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역)
        );
    }

    @Test
    @DisplayName("노선의 중간 역이 포함된 구간을 제거한다.")
    void deleteMiddleSection() {
        // given
        sections.addSection(Fixtures.createSection(4L, line, Fixtures.미금역, Fixtures.광교역, 10), line);

        // when
        sections.deleteSection(Fixtures.미금역.getId(), line);
        List<Station> stations = sections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(3),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역, Fixtures.광교역)
        );
    }

    @Test
    @DisplayName("노선에 속한 구간이 2개 이상이 아닐 경우 삭제 시도 시 예외를 던진다.")
    void deleteSectionWhenLineHasLessThanTwoSections() {
        // given
        sections.deleteSection(Fixtures.미금역.getId(), line);

        // then
        assertThatThrownBy(() -> sections.deleteSection(Fixtures.정자역.getId(), line))
            .isInstanceOf(CannotDeleteSectionException.class);
    }

    @Test
    @DisplayName("노선에 속하지 않는 하행역을 삭제하려 할 경우 예외를 던진다.")
    void deleteNonExistingDownStation() {
        // then
        assertThatThrownBy(() -> sections.deleteSection(Fixtures.광교역.getId(), line))
            .isInstanceOf(CannotDeleteSectionException.class);
    }

    @Test
    @DisplayName("노선의 최상행역을 삭제하려 할 경우 예외를 던진다.")
    void deleteUpmostStation() {
        // then
        assertThatThrownBy(() -> sections.deleteSection(Fixtures.정자역.getId(), line))
            .isInstanceOf(CannotDeleteSectionException.class);
    }

    @Test
    @DisplayName("정렬된 상태의 역 목록을 가져온다.")
    void getStations() {
        // when
        List<Station> stations = sections.getStations();

        // then
        assertAll(
            () -> assertThat(stations).hasSize(3),
            () -> assertThat(stations).containsExactly(Fixtures.판교역, Fixtures.정자역, Fixtures.미금역)
        );
    }

    private static Stream<Arguments> provideInvalidSectionSource() {
        Line line = new Line("새로운노선", "새로운색깔");
        Station 새로운역1 = Fixtures.createStation(100L, "새로운역1");
        Station 새로운역2 = Fixtures.createStation(101L, "새로운역2");

        return Stream.of(
            Arguments.of(Fixtures.createSection(10L, line, Fixtures.판교역, Fixtures.미금역, 10), line, "구간 내 역 모두 존재"),
            Arguments.of(Fixtures.createSection(10L, line, 새로운역1, 새로운역2, 10), line, "구간 내 역 존재 x"),
            Arguments.of(Fixtures.createSection(10L, line, Fixtures.판교역, Fixtures.광교역, 10), line, "구간 길이의 합 조건 만족 x")
        );
    }
}
