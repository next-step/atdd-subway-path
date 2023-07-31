package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    Line line = new Line("신분당선", "red");
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);
    Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

    @Test
    @DisplayName("노선의 역목록 출력 기능")
    void getStations() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간, 양재_양재시민의숲_구간)
                .collect(Collectors.toList()));

        // when then
        assertThat(sections.getStations()).containsExactly(논현역, 양재역, 양재시민의숲역);
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하지 않는 경우")
    void addSectionException_withoutStations() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 신사역, 양재시민의숲역, 5);

        assertThatThrownBy(() -> {
            sections.addSection(section);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_NOT_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하는 경우")
    void addSectionException_hasAllStations() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 양재역, 논현역, 5);

        assertThatThrownBy(() -> {
            sections.addSection(section);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_EXIST_IN_LINE.getMessage());
    }
}
