package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.unit.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {
    Line line = new Line("신분당선", "red");
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);
    Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

    @Test
    void add() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        // when
        sections.add(line, 양재역, 양재시민의숲역, 10);

        // then
        assertThat(sections.getSections()).contains(양재_양재시민의숲_구간);
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
            sections.add(line, 논현역, 양재역, 10);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }
}
