package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import nextstep.subway.exception.SectionDeleteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    Line line;

    @BeforeEach
    public void setUp() {
        line = new Line("신분당선", "red");
        Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);
        line.getSections().add(논현_양재_구간);
    }

    @Test
    @DisplayName("구간 추가 기능")
    void addSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

        // when
        line.addSection(양재역, 양재시민의숲역, 10);

        // then
        assertThat(line.getSections()).contains(양재_양재시민의숲_구간);
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하지 않는 경우")
    void addSectionException_withoutStations() {
        // then
        assertThatThrownBy(() -> {
            line.addSection(신사역, 강남역, 10);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_NOT_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하는 경우")
    void addSectionException_hasAllStations() {
        // then
        assertThatThrownBy(() -> {
            line.addSection(논현역, 양재역, 10);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("기존 구간 사이에 추가하려는 구간의 거리가 기존 구간의 거리 보다 긴 경우")
    void addSectionException_distanceToLong() {
        // then
        assertThatThrownBy(() -> {
            line.addSection(논현역, 양재역, 10);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }

    @Test
    @DisplayName("새로운 구간을 상행 종점으로 추가하는 경우")
    void addSectionAtFirst() {
        // given
        Section 신사_논현_구간 = new Section(line, 신사역, 논현역, 10);

        // when
        line.addSection(신사역, 논현역, 10);

        // then
        assertThat(line.getSections()).contains(신사_논현_구간);
        assertThat(line.getStations()).containsExactly(신사역, 논현역, 양재역);
    }

    @Test
    @DisplayName("새로운 구간을 하행 종점으로 추가하는 경우")
    void addSectionAtLast() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

        // when
        line.addSection(양재역, 양재시민의숲역, 10);

        // then
        assertThat(line.getSections()).contains(양재_양재시민의숲_구간);
        assertThat(line.getStations()).containsExactly(논현역, 양재역, 양재시민의숲역);
    }

    @ParameterizedTest
    @DisplayName("기준 구간 사이에 구간을 추가하는 경우")
    @MethodSource("provideSections")
    void addSectionAtMiddle(Station upStation, Station downStation, int distance) {
        // given
        Section 논현_강남_구간 = new Section(line, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(line, 강남역, 양재역, 6);

        // when
        line.addSection(upStation, downStation, distance);

        // then
        assertThat(line.getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
        assertThat(line.getStations()).containsExactly(논현역, 강남역, 양재역);
    }

    public static Stream<Arguments> provideSections() {
        return Stream.of(
                Arguments.of(논현역, 강남역, 4),
                Arguments.of(강남역, 양재역, 6)
        );
    }

    @Test
    @DisplayName("구간 삭제 기능")
    void removeSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);
        line.getSections().add(양재_양재시민의숲_구간);

        // when
        line.removeSection(양재시민의숲역);

        // then
        assertThat(line.getSections()).doesNotContain(양재_양재시민의숲_구간);
    }

    @ParameterizedTest
    @MethodSource("provideStations")
    @DisplayName("구간이 하나일 때 역을 제거하는 경우")
    void removeSection_leftOnlyOneSection(Station station) {
        // when
        assertThatThrownBy(() -> {
            line.removeSection(station);
        }).isInstanceOf(SectionDeleteException.class)
                .hasMessage(ErrorType.CANNOT_REMOVE_LAST_SECTION.getMessage());
    }

    public static Stream<Arguments> provideStations() {
        return Stream.of(
                Arguments.of(논현역),
                Arguments.of(양재역)
        );
    }

    @Test
    @DisplayName("중간역 구간 삭제")
    void removeMiddleSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);
        line.getSections().add(양재_양재시민의숲_구간);
        Section 논현_양재시민의숲_구간 = new Section(line, 논현역, 양재시민의숲역, 20);

        // when
        line.removeSection(양재역);

        // then
        assertThat(line.getSections()).containsExactly(논현_양재시민의숲_구간);
    }

    @Test
    @DisplayName("상행종점역 구간 삭제")
    void removeFirstSection() {
        // given
        Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);
        line.getSections().add(양재_양재시민의숲_구간);

        // when
        line.removeSection(논현역);

        // then
        assertThat(line.getSections()).containsExactly(양재_양재시민의숲_구간);
    }
}
