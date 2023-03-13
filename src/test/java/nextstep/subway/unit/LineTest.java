package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line 경강선;
    private Station 부발역;
    private Station 여주역;
    private Station 이천역;

    @BeforeEach
    void setup() {
        경강선 = new Line("경강선", "blue");
        부발역 = new Station("부발역");
        여주역 = new Station("여주역");
        이천역 = new Station("이천역");
    }

    @DisplayName("노선에 새로운 구간을 추가할 경우 구간이 추가 된다.")
    @Test
    void addSection() {
        // when
        경강선.addSection(여주역, 부발역, 10);

        // then
        assertThat(경강선.getStations()).contains(여주역, 부발역);
    }

    @DisplayName("노선에 포함된 모든 역들을 조회할 수 있다.")
    @Test
    void getStations() {
        // when
        경강선.addSection(여주역, 부발역, 10);

        // then
        assertThat(경강선.getStations()).containsExactly(여주역, 부발역);
    }


    @DisplayName("노선에 포함된 구간을 제거할 수 있다.")
    @Test
    void removeSection() {
        // given
        경강선.addSection(여주역, 부발역, 10);
        경강선.addSection(부발역, 이천역, 10);

        // when
        경강선.deleteSection(이천역);

        // then
        assertThat(경강선.getStations()).isNotIn(이천역);
    }
}
