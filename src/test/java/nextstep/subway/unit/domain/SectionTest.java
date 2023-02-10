package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[Domain] 지하철 구간 테스트")
class SectionTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private Section 교대역_강남역;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        교대역 = new Station(3L, "교대역");

        line = new Line("2호선", "green");

        교대역_강남역 = new Section(line, 교대역, 강남역, 10);
    }

    @DisplayName("상행역이 동일한 경우 기존 구간을 기준으로 오른쪽 구간이 생성된다.")
    @Test
    void to1() {
        Section addSection = new Section(line, 교대역, 역삼역, 3);

        Section upSection = 교대역_강남역.to(addSection);

        assertAll(() -> {
            assertThat(upSection.getUpStation()).isEqualTo(역삼역);
            assertThat(upSection.getDownStation()).isEqualTo(강남역);
            assertThat(upSection.getDistance()).isEqualTo(7);
        });
    }

    @DisplayName("하행역이 동일한 경우 기존 구간을 기준으로 왼쪽 구간이 생성된다.")
    @Test
    void to2() {
        Section addSection = new Section(line, 역삼역, 강남역, 8);

        Section upSection = 교대역_강남역.to(addSection);

        assertAll(() -> {
            assertThat(upSection.getUpStation()).isEqualTo(교대역);
            assertThat(upSection.getDownStation()).isEqualTo(역삼역);
            assertThat(upSection.getDistance()).isEqualTo(2);
        });
    }

    @DisplayName("구간끼리 역이 동일한지 확인할 수 있다.")
    @Test
    void isSameStations() {
        Section sectionA = new Section(null, 강남역, 역삼역, 5);
        Section sectionB = new Section(null, 강남역, 역삼역, 10);

        assertThat(sectionA.isSameUpStation(sectionB)).isTrue();
    }
}
