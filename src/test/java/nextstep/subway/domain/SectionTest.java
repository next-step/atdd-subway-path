package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Section unit test")
class SectionTest {

    private Section section;
    private Station 강남역;
    private Station 역삼역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        section = new Section(강남역, 역삼역, 10);
    }

    @ParameterizedTest
    @MethodSource
    void containStation(Station station, Boolean result) {
        assertThat(section.containStation(station)).isEqualTo(result);
    }

    static Stream<Arguments> containStation() {
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 없는역 = new Station("업는역");
        return Stream.of(
                Arguments.of(강남역, Boolean.TRUE),
                Arguments.of(역삼역, Boolean.TRUE),
                Arguments.of(없는역, Boolean.FALSE)
        );
    }
}