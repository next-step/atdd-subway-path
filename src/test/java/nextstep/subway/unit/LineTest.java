package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("구간 단위 테스트")
class LineTest {
    private Line line;
    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        교대역 = new Station(3L, "교대역");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);

        line.add(강남역_역삼역_구간);

        assertAll(() -> {
            assertThat(line.getSections()).hasSize(1);
            assertThat(강남역_역삼역_구간.getLine()).isEqualTo(line);
        });
    }

    @DisplayName("노선에 포함된 역을 찾을 수 있다.")
    @Test
    void getStations() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);
        Section 역삼역_교대역_구간 = new Section(역삼역, 교대역, 10);

        line.add(강남역_역삼역_구간);
        line.add(역삼역_교대역_구간);

        assertThat(line.getStations())
                .containsExactly(강남역, 역삼역, 교대역);
    }

    @DisplayName("노선에 있는 구간을 제거할 수 있다.")
    @Test
    void removeSection() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);
        Section 역삼역_교대역_구간 = new Section(역삼역, 교대역, 10);
        line.add(강남역_역삼역_구간);
        line.add(역삼역_교대역_구간);

        line.removeSection(교대역.getId());

        assertThat(line.getSections()).containsExactly(강남역_역삼역_구간);
    }
}
