package nextstep.subway.domain;

import nextstep.subway.exception.section.MinimumDistanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 도메인 테스트")
class SectionTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
    }

    @DisplayName("상행역과 구간의 길이를 변경할 수 있다")
    @ValueSource(ints = {1, 2, 3, 10, 30, 100})
    @ParameterizedTest
    void updateUpStation(int distance) {
        // given
        Section target = new Section(강남역, 판교역, 5);

        // when
        target.updateUpStation(정자역, distance);

        // then
        assertThat(target.getUpStation()).isEqualTo(정자역);
        assertThat(target.getDownStation()).isEqualTo(판교역);
        assertThat(target.getDistance()).isEqualTo(distance);
    }

    @DisplayName("상행역 변경 시, 구간의 길이는 1 이상이어야 한다")
    @ValueSource(ints = {-100, -10, -5, -3, -2, -1, 0})
    @ParameterizedTest
    void updateUpStation_fail(int distance) {
        // given
        Section target = new Section(강남역, 판교역, 5);

        // then
        assertThatThrownBy(() -> target.updateUpStation(판교역, distance))
                .isInstanceOf(MinimumDistanceException.class);
    }

    @DisplayName("하행역과 구간의 길이를 변경할 수 있다")
    @ValueSource(ints = {1, 2, 3, 10, 30, 100})
    @ParameterizedTest
    void updateDownStation(int distance) {
        // given
        Section target = new Section(강남역, 판교역, 5);

        // when
        target.updateDownStation(정자역, distance);

        // then
        assertThat(target.getUpStation()).isEqualTo(강남역);
        assertThat(target.getDownStation()).isEqualTo(정자역);
        assertThat(target.getDistance()).isEqualTo(distance);
    }

    @DisplayName("상행역 변경 시, 구간의 길이는 1 이상이어야 한다")
    @ValueSource(ints = {-100, -10, -5, -3, -2, -1, 0})
    @ParameterizedTest
    void updateDownStation_fail(int distance) {
        // given
        Section target = new Section(강남역, 판교역, 5);

        // then
        assertThatThrownBy(() -> target.updateDownStation(판교역, distance))
                .isInstanceOf(MinimumDistanceException.class);
    }

}