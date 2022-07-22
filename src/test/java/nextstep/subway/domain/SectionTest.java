package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    Line line;
    Section section;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", "green");
        section = new Section(line, new Station(1L, "강남역"), new Station(2L, "잠실역"), 10);
    }

    @Test
    @DisplayName("기존 상행 종점역과 같은지 검증")
    void isTwoUpStationsSame() {
        //when
        final boolean actual = section.isSameUpStation(new Station(1L, "강남역"));

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("기존 하행 종점역과 같은지 검증")
    void isTwoDownStationsSame() {
        //when
        final boolean actual = section.isSameDownStation(new Station(2L, "잠실역"));

        //then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("기존 구간의 거리보다 새로 들어온 구간의 거리가 크다면 예외")
    void addMiddleSectionFrontException() {
        //when
        final Section newSection = new Section(line, new Station(1L, "강남역"), new Station("역삼역"), 15);

        //then
        assertThatIllegalStateException().isThrownBy(() -> section.addMiddleSection(newSection))
            .withMessage("기존의 거리가 새로운 거리보다 작습니다.");
    }

    @Test
    @DisplayName("상행역 기준 가운데 구간 추가")
    void addMiddleSectionFront() {
        //when
        final Section newSection = new Section(line, new Station(1L, "강남역"), new Station(3L, "역삼역"), 7);
        section.addMiddleSection(newSection);

        //then
        assertThat(section.getUpStation()).isEqualTo(new Station(3L, "역삼역"));
        assertThat(section.getDistance()).isEqualTo(3);
    }

    @Test
    @DisplayName("하행역 기준 - 기존 구간의 거리보다 새로 들어온 구간의 거리가 크다면 예외")
    void addMiddleSectionBackException() {
        //when
        final Section newSection = new Section(line, new Station(3L, "역삼역"), new Station(2L, "잠실역"), 11);

        //then
        assertThatIllegalStateException().isThrownBy(() -> section.addMiddleSection(newSection))
            .withMessage("기존의 거리가 새로운 거리보다 작습니다.");
    }

    @Test
    @DisplayName("하행역 기준으로 가운데 구간 추가")
    void addMiddleSectionBack() {
        //when
        final Section newSection = new Section(line, new Station(3L, "역삼역"), new Station(2L, "잠실역"), 7);
        section.addMiddleSection(newSection);

        //then
        assertThat(section.getDownStation()).isEqualTo(new Station(3L, "역삼역"));
        assertThat(section.getDistance()).isEqualTo(3);
    }

}