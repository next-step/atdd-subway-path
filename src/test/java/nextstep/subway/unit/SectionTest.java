package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    private Line 칠호선;
    private Station 남성역;
    private Station 상도역;
    private int 남성역_상도역_거리;
    private Section 남성역_상도역_구간;

    @BeforeEach
    void setUp() {
        // given
        칠호선 = new Line("7호선", "green darken-2");
        남성역 = new Station("남성역");
        상도역 = new Station("상도역");
        남성역_상도역_거리 = 15;
        남성역_상도역_구간 = new Section(칠호선, 남성역, 상도역, 남성역_상도역_거리);
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() {
        // when
        final Station 숭실대입구역 = new Station("숭실대입구역");
        final Section 숭실대입구역_상도역_구간 = 남성역_상도역_구간.addStation(숭실대입구역, 7);

        // then
        assertAll(() -> {
            assertThat(남성역_상도역_구간.getDownStation()).isEqualTo(숭실대입구역);
            assertThat(남성역_상도역_구간.getDistance()).isEqualTo(8);
            assertThat(숭실대입구역_상도역_구간).isEqualTo(new Section(칠호선, 숭실대입구역, 상도역, 7));
        });
    }

    @Test
    @DisplayName("구간을 수정한다.")
    void modifySection() {
        // given
        final Station 숭실대입구역 = new Station("숭실대입구역");
        final Section 숭실대입구역_상도역_구간 = 남성역_상도역_구간.addStation(숭실대입구역, 7);

        // when
        남성역_상도역_구간.modifyUpStation(숭실대입구역_상도역_구간);

        // then
        assertAll(() -> {
            assertThat(남성역_상도역_구간.getUpStation()).isEqualTo(숭실대입구역);
            assertThat(남성역_상도역_구간.getDistance()).isEqualTo(15);
        });
    }

    @Test
    @DisplayName("구간의 하행역이 맞는지 확인한다.")
    void isDownStation() {
        // when
        // then
        assertThat(남성역_상도역_구간.isDownStation(상도역)).isTrue();
    }

    @Test
    @DisplayName("구간의 상행역이 맞는지 확인한다.")
    void isUpStation() {
        // when
        // then
        assertThat(남성역_상도역_구간.isUpStation(남성역)).isTrue();
    }
}
