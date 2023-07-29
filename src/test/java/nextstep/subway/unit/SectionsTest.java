package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    Line line = new Line("신분당선", "red");
    Section 신사_논현_구간 = new Section(line, 신사역, 논현역, 10);
    Section 논현_양재_구간 = new Section(line, 논현역, 양재역, 10);
    Section 양재_양재시민의숲_구간 = new Section(line, 양재역, 양재시민의숲역, 10);

    @Test
    @DisplayName("첫번째 구간으로 등록")
    void addFirst() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 신사역, 논현역, 10);

        // when
        sections.addFirst(section);

        // then
        assertThat(sections.getSections().get(0)).isEqualTo(신사_논현_구간);
    }

    @Test
    @DisplayName("마지막 구간으로 등록")
    void addLast() {
        // given
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 양재역, 양재시민의숲역, 10);

        // when
        sections.addLast(section);

        // then
        assertThat(sections.getSections().get(1)).isEqualTo(양재_양재시민의숲_구간);
    }

    @Test
    @DisplayName("상행역을 기준으로 중간 구간 등록")
    void addMiddleUpStation() {
        // given
        Section 논현_강남_구간 = new Section(line, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(line, 강남역, 양재역, 6);
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 논현역, 강남역, 4);

        // when
        sections.addMiddleUpStation(section);

        // then
        assertThat(sections.getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }

    @Test
    @DisplayName("하행역을 기준으로 중간 구간 등록")
    void addMiddleDownStation() {
        // given
        Section 논현_강남_구간 = new Section(line, 논현역, 강남역, 4);
        Section 강남_양재_구간 = new Section(line, 강남역, 양재역, 6);
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 강남역, 양재역, 6);

        // when
        sections.addMiddleDownStation(section);

        // then
        assertThat(sections.getSections()).containsExactly(논현_강남_구간, 강남_양재_구간);
    }

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
    @DisplayName("새로운 구간을 상행 종점으로 추가하려는 경우")
    void addSectionException_distanceToLong() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));
        Section section = new Section(line, 논현역, 양재역, 10);

        // then
        assertThatThrownBy(() -> {
            sections.addMiddleDownStation(section);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }

    @ParameterizedTest
    @DisplayName("구간 추가 방식 찾기")
    @MethodSource("provideSections")
    void findAddType(Station upStation, Station downStation, SectionAddType type) {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        assertThat(sections.findAddType(upStation, downStation)).isEqualTo(type);
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
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하지 않는 경우")
    void addSectionException_withoutStations() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        assertThatThrownBy(() -> {
            sections.findAddType(신사역, 양재시민의숲역);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_NOT_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("추가하려는 구간의 모든 역이 노선에 존재하는 경우")
    void addSectionException_hasAllStations() {
        Sections sections = new Sections(Stream.of(논현_양재_구간).collect(Collectors.toList()));

        assertThatThrownBy(() -> {
            sections.findAddType(양재역, 논현역);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_EXIST_IN_LINE.getMessage());
    }

    @Test
    @DisplayName("하행종점역 구간 삭제")
    void removeLast() {
        Sections sections = new Sections(Stream.of(논현_양재_구간, 양재_양재시민의숲_구간).collect(Collectors.toList()));

        sections.removeLast(양재시민의숲역);

        assertThat(sections.getSections()).doesNotContain(양재_양재시민의숲_구간);
    }
}
