package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.enums.SubwayErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    @DisplayName("제거대상의 구간과 제거 대상과 연결되어 있는 구간의 거리를 더한다")
    void addDistance() {
        //given
        Distance originDistance = new Distance(10);
        Distance newDistance = new Distance(7);

        //when
        Distance addedDistance = originDistance.getAddedDistanceBy(newDistance);

        //then
        assertThat(addedDistance.getValue()).isEqualTo(17);
    }

    @Test
    @DisplayName("기존 구간의 거리에서 새롭게 추가할 구간의 거리를 뺀다")
    void decreaseDistance() {
        //given
        Distance originDistance = new Distance(10);
        Distance newDistance = new Distance(5);

        //when
        Distance decreaseDistance = originDistance.getDecreaseDistanceBy(newDistance);

        //then
        assertThat(decreaseDistance.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("거리 감소 실패 테스트 - 기존 거리보다 긴 거리를 뺀다")
    void decreaseDistanceFailed() {
        //given
        Distance originDistance = new Distance(10);
        Distance newDistance = new Distance(11);

        //when, then
        assertThatThrownBy(() -> originDistance.getDecreaseDistanceBy(newDistance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.INVALID_DISTANCE.getMessage());
    }

}