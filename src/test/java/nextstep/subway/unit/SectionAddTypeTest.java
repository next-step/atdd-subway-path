package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SectionAddTypeTest {
    Line line = new Line("신분당선", "red");
    Section section = new Section(line, 논현역, 양재역, 10);
    Sections sections = new Sections(List.of(section));

    @Test
    void findAddTypeEmptyStation() {
        Sections sections = new Sections(new ArrayList<>());
        assertThat(SectionAddType.find(sections, 논현역, 양재역)).isEqualTo(SectionAddType.LAST);
    }

    @ParameterizedTest
    @MethodSource("provideSections")
    void findAddType(Station upStation, Station downStation, SectionAddType type) {
        assertThat(SectionAddType.find(sections, upStation, downStation)).isEqualTo(type);
    }

    public static Stream<Arguments> provideSections() {
        return Stream.of(
                Arguments.of(신사역, 논현역, SectionAddType.FIRST),
                Arguments.of(양재역, 양재시민의숲역, SectionAddType.LAST),
                Arguments.of(논현역, 강남역, SectionAddType.MIDDLE_UP_STATION),
                Arguments.of(강남역, 양재역, SectionAddType.MIDDLE_DOWN_STATION)
        );
    }

    @Test
    void addSectionException_withoutStations() {
        assertThatThrownBy(() -> {
            SectionAddType.find(sections, 신사역, 양재시민의숲역);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_NOT_EXIST_IN_LINE.getMessage());
    }

    @Test
    void addSectionException_hasAllStations() {
        assertThatThrownBy(() -> {
            SectionAddType.find(sections, 양재역, 논현역);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_EXIST_IN_LINE.getMessage());
    }
}
