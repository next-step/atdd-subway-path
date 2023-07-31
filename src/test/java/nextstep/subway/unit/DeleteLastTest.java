package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.delete.DeleteLast;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteLastTest {
    Line line = new Line("신분당선", "red");
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);
    Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

    @Test
    @DisplayName("하행종점역 구간 삭제")
    void removeLast() {
        Sections sections = new Sections(Stream.of(논현_양재_구간, 양재_양재시민의숲_구간).collect(Collectors.toList()));

        new DeleteLast().delete(sections, 양재시민의숲역);

        assertThat(sections.getSections()).doesNotContain(양재_양재시민의숲_구간);
    }
}
