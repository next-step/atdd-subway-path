package nextstep.subway.unit;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("역 사이에 새로운 역을 등록할 수 있다.")
    @Test
    void addBetweenSection() {
        // given
        경강선.addSection(여주역, 부발역, 10);

        // when
        경강선.addSection(여주역, 이천역, 5);

        // then
        assertThat(경강선.getStations()).containsExactly(여주역, 이천역, 부발역);
    }


    @DisplayName("역 사이에 새로운 역을 등록할 할 때, 기존의 역 길이보다 새로운 역의 길이가 크거나 같다면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void addBetweenSectionException2(int distance) {
        // given
        경강선.addSection(여주역, 부발역, 10);

        // when & then

        assertThatThrownBy(()-> 경강선.addSection(여주역, 이천역, distance))
                .isInstanceOf(SubwayException.class);

    }

    @DisplayName("새로운 역을 상행 종점역으로 등록할 수 있다.")
    @Test
    void upStationAddSection() {
        // given
        경강선.addSection(여주역, 부발역, 10);

        // when
        경강선.addSection(이천역, 여주역, 4);

        // then
        assertThat(경강선.getStations().get(0)).isEqualTo(이천역);
    }

    @DisplayName("새로운 역을 하행 종점역으로 등록할 수 있다.")
    @Test
    void downStationAddSection() {
        // given
        경강선.addSection(여주역, 부발역, 10);

        // when
        경강선.addSection(부발역, 이천역, 10);

        // then
        assertThat(경강선.getStations()).containsExactly(여주역, 부발역, 이천역);
    }

    @DisplayName("등록하려는 상행역과 하행역이 이미 노선에 모두 포함되어 있다면 예외가 발생한다.")
    @Test
    void addSectionException1() {
        // given
        경강선.addSection(여주역, 부발역, 10);

        // when & then
        assertThatThrownBy(()->
                경강선.addSection(여주역, 부발역, 10)).isInstanceOf(SubwayException.class);
    }

    @DisplayName("등록하려는 상행역과 하행역 둘 중 하나도 포함되어 있지 않다면 예외가 발생한다.")
    @Test
    void addSectionException2() {
        // given
        Station 신둔도예촌역 = new Station("신둔도예촌역");
        경강선.addSection(여주역, 부발역, 10);

        // when & then
        assertThatThrownBy(() ->
                경강선.addSection(이천역, 신둔도예촌역, 3)).isInstanceOf(SubwayException.class);

    }
 }
