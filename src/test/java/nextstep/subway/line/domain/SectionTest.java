package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    @ParameterizedTest
    @CsvSource(value = {"대방역:true", "신길역:true", "노량진역:false"}, delimiter = ':')
    public void containTest(String stationName, boolean expected) {
        //Given
        Section section = new Section(new Line("1호선", "블루"), new Station("대방역"), new Station("신길역"), 10);

        //When
        boolean result = section.containsStation(new Station(stationName));

        //Then
        assertThat(expected).isEqualTo(result);
    }

    @DisplayName("새로 등록되는 구간의 하행역을 기존 구간의 상행역으로 교체")
    @ParameterizedTest
    @MethodSource("provideNewAddSectionCase1")
    public void changeUpStation(Station upStation, Station newDownStation, int originalDistance, int newDistance) {
        //Given
        Section registeredSection = new Section(new Line("1호선", "블루"), upStation, new Station("노량진역"), originalDistance);
        Section newSection = new Section(new Line("1호선", "블루"), upStation, newDownStation, newDistance);

        //When
        registeredSection.changeUpStation(newSection);

        //Then
        assertAll(
                () -> assertThat(registeredSection.getUpStation()).isEqualTo(newDownStation),
                () -> assertThat(registeredSection.getDistance()).isEqualTo(originalDistance - newDistance)
        );
    }

    @DisplayName("새로 등록되는 구간의 상행역을 기존 구간의 하행역으로 교체")
    @ParameterizedTest
    @MethodSource("provideNewAddSectionCase2")
    public void changeDownStation(Station newUpStation, Station newDownStation, int originalDistance, int newDistance) {
        //Given
        Section registeredSection = new Section(new Line("1호선", "블루"), new Station("신길"), new Station("노량진역"), originalDistance);
        Section newSection = new Section(new Line("1호선", "블루"), newUpStation, newDownStation, newDistance);

        //When
        registeredSection.changeDownStation(newSection);

        //Then
        assertAll(
                () -> assertThat(registeredSection.getDownStation()).isEqualTo(newUpStation),
                () -> assertThat(registeredSection.getDistance()).isEqualTo(originalDistance - newDistance)
        );
    }

    private static Stream<Arguments> provideNewAddSectionCase1() {
        return Stream.of(
                Arguments.of(new Station("신길역"), new Station("대방역"), 10, 5)
        );
    }

    private static Stream<Arguments> provideNewAddSectionCase2() {
        return Stream.of(
                Arguments.of(new Station("대방역"), new Station("노량진역"), 10, 3)
        );
    }
}
