package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    private Station 강남역;
    private Station 역삼역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("구간 수정")
    @Test
    void update() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);

        // when
        강남역_역삼역_구간.update(역삼역, 강남역, 3);

        // then
        assertThat(강남역_역삼역_구간.getUpStation()).isEqualTo(역삼역);
        assertThat(강남역_역삼역_구간.getDownStation()).isEqualTo(강남역);
        assertThat(강남역_역삼역_구간.getDistance()).isEqualTo(3);
    }
}
