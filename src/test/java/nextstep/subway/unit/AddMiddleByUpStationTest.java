package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.add.AddMiddleByUpStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AddMiddleByUpStationTest {
    Line line = new Line("신분당선", "red");
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);

    @Test
    @DisplayName("상행역을 기준으로 중간 구간 등록")
    void addMiddleUpStation() {
        // given
        Section 논현_강남_구간 = new Section(line, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(line, 강남역, 양재역, 6);
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 논현역, 강남역, 4);

        // when
        new AddMiddleByUpStation().add(sections, section);

        // then
        assertThat(sections.getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }
}
