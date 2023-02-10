package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.unit.Fixtures;

class SectionTest {

    Line line;
    Section section;

    @BeforeEach
    void setup() {
        line = new Line("새로운노선", "새로운색깔");
        section = Fixtures.createSection(1L, line, Fixtures.판교역, Fixtures.정자역, 10);
    }

    @Test
    @DisplayName("Section 객체를 생성한다.")
    void create() {
        assertThat(section.getId()).isEqualTo(1L);
        assertThat(section.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("주어진 id를 통해 상행역이 맞는지를 체크한다.")
    void isSameUpStation() {
        assertThat(section.isSameUpStation(Fixtures.판교역.getId())).isTrue();
    }

    @Test
    @DisplayName("주어진 id를 통해 하행역이 맞는지를 체크한다.")
    void isSameDownStation() {
        assertThat(section.isSameDownStation(Fixtures.정자역.getId())).isTrue();
    }
}
