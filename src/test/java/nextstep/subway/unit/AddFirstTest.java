package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.add.AddFirst;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AddFirstTest {
    Line line = new Line("신분당선", "red");
    Section 신사_논현_구간 = new Section(line, 신사역, 논현역, 10);
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);

    @Test
    @DisplayName("첫번째 구간으로 등록")
    void addFirst() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 신사역, 논현역, 10);

        // when
        new AddFirst().add(sections, section);

        // then
        assertThat(sections.getSections().get(0)).isEqualTo(신사_논현_구간);
    }
}
