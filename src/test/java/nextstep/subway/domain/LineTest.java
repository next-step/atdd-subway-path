package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private static Stream<Arguments> provideLineAndStations() {
        Station startingStation = new Station(1L, "강남역");
        Station endingStation = new Station(2L, "사당역");
        int distance = 10;
        Line line = new Line(1L, "2호선", "green", startingStation, endingStation, distance);

        return Stream.of(Arguments.of(line, startingStation, endingStation));
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @ParameterizedTest
    @MethodSource("provideLineAndStations")
    void addSection(Line line, Station startingStation, Station endingStation) {
        // given
        // when
        Station newEndingStation = new Station(3L, "신림역");
        int distance = 10;
        line.addSection(endingStation, newEndingStation, distance);

        // then
        assertThat(line.getStations()).isEqualTo(Arrays.asList(startingStation, endingStation, newEndingStation));
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @ParameterizedTest
    @MethodSource("provideLineAndStations")
    void getStations(Line line, Station startingStation, Station endingStation) {
        // given
        // when
        // then
        assertThat(line.getStations()).isEqualTo(Arrays.asList(startingStation, endingStation));
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @ParameterizedTest
    @MethodSource("provideLineAndStations")
    void removeSection(Line line, Station startingStation, Station endingStation) {
        // given
        Station newEndingStation = new Station(3L, "신림역");
        int distance = 10;
        line.addSection(endingStation, newEndingStation, distance);

        // when
        line.removeSection(newEndingStation);

        // then
        assertThat(line.getStations()).isEqualTo(Arrays.asList(startingStation, endingStation));
    }
}
