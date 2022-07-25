package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.CannotSubtractSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    private static final Line LINE = new Line("신분당선", "red");

    @DisplayName("상행역, 하행역중 하나가 겹치는 구간 둘이 있을 때 긴 구간에서 짧은 구간을 뺀 구간을 구할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideArgumentsForSubTract")
    void subtract(Section longSection, Section shortSection, int expected) {
        Section subtractedSection = longSection.subtract(shortSection);

        assertThat(subtractedSection.getDistance()).isEqualTo(expected);
    }

    private static Stream<Arguments> provideArgumentsForSubTract() {
        return Stream.of(
                Arguments.of(new Section(LINE, 1L, 2L, 4),
                        new Section(LINE, 1L, 3L, 3),
                        1),
                Arguments.of(new Section(LINE, 1L, 2L, 5),
                        new Section(LINE, 3L, 2L, 2),
                        3)
        );
    }

    @DisplayName("상행역, 하행역중 하나도 겹치지 않는 구간끼리는 뺄 수 없다.")
    @ParameterizedTest
    @MethodSource("provideArgumentsForSubTract_Exception")
    void subtract_Exception(Section longSection, Section shortSection) {
        assertThatThrownBy(() -> longSection.subtract(shortSection))
                .isInstanceOf(CannotSubtractSectionException.class)
                .hasMessage("상행역이나 하행역이 겹치는 구간끼리만 뺄 수 있습니다.");
    }

    private static Stream<Arguments> provideArgumentsForSubTract_Exception() {
        return Stream.of(
                Arguments.of(new Section(LINE, 1L, 2L, 4),
                        new Section(LINE, 2L, 3L, 3)),
                Arguments.of(new Section(LINE, 1L, 2L, 5),
                        new Section(LINE, 3L, 4L, 2)));
    }

    @DisplayName("두 구간의 상행역이나 하행역중 하나가 똑같은지 알 수 있다.")
    @Test
    void startsOrEndsTogether() {
        Section section12 = new Section(LINE, 1L, 2L, 10);
        Section section32 = new Section(LINE, 3L, 2L, 10);
        Section section14 = new Section(LINE, 1L, 4L, 10);

        assertThat(section12.startsOrEndsTogether(section32)).isTrue();
        assertThat(section12.startsOrEndsTogether(section14)).isTrue();
        assertThat(section32.startsOrEndsTogether(section14)).isFalse();
    }

    @DisplayName("구간의 상행역이나 하행역의 일치 여부를 알 수 있다.")
    @Test
    void matchUpStation_matchDownStation() {
        Section section = new Section(LINE, 1L, 2L, 4);

        assertThat(section.matchUpStation(1L)).isTrue();
        assertThat(section.matchDownStation(1L)).isFalse();

        assertThat(section.matchUpStation(2L)).isFalse();
        assertThat(section.matchDownStation(2L)).isTrue();
    }

    @DisplayName("두 구간이 이어질 때 앞/뒤를 구분할 수 있다.")
    @Test
    void isBefore_isAfter() {
        Section beforeSection = new Section(LINE, 1L, 2L, 3);
        Section afterSection = new Section(LINE, 2L, 3L, 4);

        assertThat(beforeSection.isBefore(afterSection)).isTrue();
        assertThat(afterSection.isBefore(afterSection)).isFalse();

        assertThat(beforeSection.isAfter(afterSection)).isFalse();
        assertThat(afterSection.isAfter(beforeSection)).isTrue();
    }
}
