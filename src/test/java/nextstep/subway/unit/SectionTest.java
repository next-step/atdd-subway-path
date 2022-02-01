package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 단위 테스트(Section)")
class SectionTest {

    private Station upStation;
    private Station downStation;
    private Line line;
    private Section section;
    private int distance;

    @BeforeEach
    void setUp() {
        upStation = new Station("upStation");
        downStation = new Station("downStation");
        line = new Line("color", "name");
        distance = 10;
        section = new Section(line, upStation, downStation, distance);
    }

    @DisplayName("특정 역이 상행선으로 등록되었는지 반환")
    @Test
    void hasUpStation() {
        // then
        assertAll(
                () -> assertThat(section.isUpStation(upStation)).isTrue(),
                () -> assertThat(section.isUpStation(downStation)).isFalse()
        );
    }

    @DisplayName("특정 역이 하행선으로 등록되었는지 반환")
    @Test
    void hasDownStation() {
        // then
        assertAll(
                () -> assertThat(section.isDownStation(upStation)).isFalse(),
                () -> assertThat(section.isDownStation(downStation)).isTrue()
        );
    }

    @DisplayName("세션간의 거리값 차이 계산")
    @Test
    void subtractDistance() {
        // given
        final Station otherFirstStation = new Station("otherFirstStation");
        final Station otherSecondStation = new Station("otherSecondStation");
        final int otherDistance = 5;
        final Section other = new Section(line, otherFirstStation, otherSecondStation, otherDistance);

        // when
        final int actual = section.subtractDistance(other);

        // then
        assertThat(actual).isEqualTo(distance - otherDistance);
    }

    @DisplayName("세션간의 거리값 차이가 음수면 예외처리")
    @Test
    void subtractDistanceException() {
        // given
        final Station otherFirstStation = new Station("otherFirstStation");
        final Station otherSecondStation = new Station("otherSecondStation");
        final int otherDistance = 10;
        final Section other = new Section(line, otherFirstStation, otherSecondStation, otherDistance);

        // then
        assertThatThrownBy(() -> section.subtractDistance(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("other distance is equal or bigger");
    }
}
