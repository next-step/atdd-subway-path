package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.add.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAddTypeTest {
    Line line = new Line("신분당선", "red");
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);

    @ParameterizedTest
    @DisplayName("구간 추가 방식 찾기")
    @MethodSource("provideSections")
    void findAddType(Station upStation, Station downStation, Class strategy) {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, upStation, downStation, 5);

        assertThat(SectionAddType.find(sections, section)).isInstanceOf(strategy);
    }

    public static Stream<Arguments> provideSections() {
        return Stream.of(
                Arguments.of(신사역, 논현역, AddFirst.class),
                Arguments.of(양재역, 양재시민의숲역, AddLast.class),
                Arguments.of(논현역, 강남역, AddMiddleByUpStation.class),
                Arguments.of(강남역, 양재역, AddMiddleByDownStation.class)
        );
    }
}
