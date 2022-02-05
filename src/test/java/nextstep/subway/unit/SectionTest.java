package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    private Line _5호선;
    private Station 군자역;
    private Station 아차산역;
    private Station 광나루역;
    private int distance;
    private Section section;

    @BeforeEach
    void setup() {
        // given
        _5호선 = new Line("5호선", "파란색");
        군자역 = new Station("군자역");
        아차산역 = new Station("아차산역");
        광나루역 = new Station("광나루역");
        distance = 10;
        section = Section.of(_5호선, 군자역, 아차산역, distance);
        _5호선.addSection(section);
    }

    @DisplayName("of 메소드는 받은 파라미터로 Section을 생성하여 반환한다.")
    @Test
    void of() {
        // when
        Section section = Section.of(_5호선, 군자역, 아차산역, distance);

        // then
        assertThat(section.getLine()).isEqualTo(_5호선);
        assertThat(section.getUpStation()).isEqualTo(군자역);
        assertThat(section.getDownStation()).isEqualTo(아차산역);
        assertThat(section.getDistance()).isEqualTo(distance);
    }
}
