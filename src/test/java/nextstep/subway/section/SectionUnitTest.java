package nextstep.subway.section;

import java.util.stream.Stream;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("지하철 구간 관리 단위 테스트")
public class SectionUnitTest {

    private static Line _2호선;
    private static Station 강남역 = new Station("강남역");
    private static Station 역삼역 = new Station("역삼역");
    private static Section 강남_역삼_구간;

    @BeforeEach
    void setUp() {
        _2호선 = new Line("_2호선", "bg-green-600", 10L);
        강남_역삼_구간 = new Section(_2호선, 강남역, 역삼역, 10L);
    }

    @DisplayName("지하철 구간이 연결되어 있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("isConnectedSectionParameters")
    void testConnectionTest(Section connectSection, boolean expected) {
        // when
        boolean actual = 강남_역삼_구간.isConnectedSection(connectSection);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> isConnectedSectionParameters() {
        Station 선릉역 = new Station("선릉역");
        Station 용산역 = new Station("용산역");

        Section 역삼_선릉_구간 = new Section(_2호선, 역삼역, 선릉역, 10L);
        Section 선릉_역삼_구간 = new Section(_2호선, 선릉역, 역삼역, 10L);
        Section 선릉_용산_구간 = new Section(_2호선, 선릉역, 용산역, 10L);

        return Stream.of(
                Arguments.of(역삼_선릉_구간, true),
                Arguments.of(선릉_역삼_구간, true),
                Arguments.of(선릉_용산_구간, false)
        );
    }


    @ParameterizedTest
    @MethodSource("notConnectedSectionParameters")
    void testInvalidConnectionTest() {
        // given

        // when

        // then

    }

    public static Stream<Arguments> notConnectedSectionParameters() {
        return Stream.of(
                Arguments.of(new Section(), new Section()),
                Arguments.of(new Section(), new Section()),
                Arguments.of(new Section(), new Section())
        );
    }
}
