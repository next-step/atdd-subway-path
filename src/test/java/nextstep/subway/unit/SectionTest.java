package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.unit.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {
    Line line = new Line("신분당선", "red");
    Section 신사_논현_구간 = new Section(line, 신사역, 논현역, 10);
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);
    Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

    @Test
    void addFirst() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        // when
        sections.addFirst(line, 신사역, 논현역, 10);

        // then
        assertThat(sections.getSections().get(0)).isEqualTo(신사_논현_구간);
    }

    @Test
    void addLast() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        // when
        sections.addLast(line, 양재역, 양재시민의숲역, 10);

        // then
        assertThat(sections.getSections().get(1)).isEqualTo(양재_양재시민의숲_구간);
    }

    @ParameterizedTest
    @MethodSource("provideSections")
    void addMiddle(Station upStation, Station downStation, int distance) {
        // given
        Section 논현_강남_구간 = new Section(line, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(line, 강남역, 양재역, 6);
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        // when
        sections.addMiddle(line, upStation, downStation, distance);

        // then
        assertThat(sections.getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }

    public static Stream<Arguments> provideSections() {
        return Stream.of(
                Arguments.of(논현역, 강남역, 4),
                Arguments.of(강남역, 양재역, 6)
        );
    }

    @Test
    void getStations() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간, 양재_양재시민의숲_구간)
                .collect(Collectors.toList()));

        // when then
        assertThat(sections.getStations()).containsExactly(논현역, 양재역, 양재시민의숲역);
    }

    @Test
    void addSectionException_distanceToLong() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        // then
        assertThatThrownBy(() -> {
            sections.addMiddle(line, 논현역, 양재역, 10);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }
}
