package nextstep.subway.domain;

import nextstep.subway.exception.section.MinimumDistanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 도메인 테스트")
class SectionTest {

    private Station 강남역;
    private Station 판교역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        신분당선 = Line.of("신분당선", "red");

        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(판교역, "id", 2L);
        ReflectionTestUtils.setField(신분당선, "id", 1L);

    }

    @DisplayName("구간의 길이를 변경할 수 있다")
    @ValueSource(ints = {1, 2, 3, 10, 30, 100})
    @ParameterizedTest
    void updateDistance(int distance) {
        // given
        Section target = Section.of(신분당선, 강남역, 판교역, 10);

        // when
        target.updateDistance(distance);

        // then
        assertThat(target.getDistance()).isEqualTo(distance);
    }

    @DisplayName("구간의 길이는 1 이상이어야 한다")
    @ValueSource(ints = {-100, -10, -5, -3, -2, -1, 0})
    @ParameterizedTest
    void updateDistance_fail(int distance) {
        // given
        Section target = Section.of(신분당선, 강남역, 판교역, 10);

        // then
        assertThatThrownBy(() -> target.updateDistance(distance))
                .isInstanceOf(MinimumDistanceException.class);
    }

}