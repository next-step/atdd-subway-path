package nextstep.subway.unit;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Line 이호선;
    private Section 구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        이호선 = new Line("2호선", "green");
        구간 = new Section(이호선, 강남역, 역삼역, 10);
    }

    @Test
    void addSection() {
        // when
        이호선.addSection(구간);

        // then
        assertThat(이호선.getSections().getSections()).hasSize(1);
    }

    @Test
    void getStations() {
        // when
        이호선.addSection(구간);

        // then
        assertThat(이호선.getSections().getStations()).containsExactly(구간.getUpStation(), 구간.getDownStation());
    }

    @Test
    void removeSection() {
        // given
        이호선.addSection(구간);

        Section 신규구간 = new Section(이호선, 역삼역, 선릉역, 15);
        이호선.addSection(신규구간);

        // when
        이호선.removeSection(신규구간.getDownStation());

        // then
        assertThat(이호선.getSections().getSections()).hasSize(1);
    }
}
