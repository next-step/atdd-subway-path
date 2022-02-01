package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @DisplayName("of 메소드는 받은 파라미터로 Section을 생성하여 반환한다.")
    @Test
    void of() {
        // given
        Line line = new Line("노선", "파란색");
        Station upStation = new Station();
        Station downStation = new Station();
        int distance = 10;

        // when
        Section section = Section.of(line, upStation, downStation, distance);

        // then
        assertThat(section.getLine()).isEqualTo(line);
        assertThat(section.getUpStation()).isEqualTo(upStation);
        assertThat(section.getDownStation()).isEqualTo(downStation);
        assertThat(section.getDistance()).isEqualTo(distance);
    }
}
