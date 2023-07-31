package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.add.AddMiddleByDownStation;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AddMiddleByDownStationTest {
    Line line = new Line("신분당선", "red");
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);

    @Test
    @DisplayName("하행역을 기준으로 중간 구간 등록")
    void addMiddleDownStation() {
        // given
        Section 논현_강남_구간 = new Section(line, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(line, 강남역, 양재역, 6);
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 강남역, 양재역, 6);

        // when
        new AddMiddleByDownStation().add(sections, section);

        // then
        assertThat(sections.getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }

    @Test
    @DisplayName("기존 구간 사이에 새로운 구간 추가 시 길이가 긴 경우")
    void addSectionException_distanceToLong() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 논현역, 양재역, 10);

        // then
        assertThatThrownBy(() -> {
            new AddMiddleByDownStation().add(sections, section);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }
}
