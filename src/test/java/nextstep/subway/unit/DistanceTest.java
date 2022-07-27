package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.enums.SubwayErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @Test
    void addDistance() {
        //given
        Distance originDistance = new Distance(10);
        Distance newDistance = new Distance(7);

        //when
        originDistance.addDistance(newDistance);

        //then
        assertThat(originDistance.getValue()).isEqualTo(17);
    }

    @Test
    void decreaseDistance() {
        //given
        Distance originDistance = new Distance(10);
        Distance newDistance = new Distance(5);

        //when
        originDistance.decreaseDistance(newDistance);

        //then
        assertThat(originDistance.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("거리 감소 실패 테스트 - 기존 거리보다 긴 거리를 뺀다")
    void decreaseDistanceFailed() {
        //given
        Distance originDistance = new Distance(10);
        Distance newDistance = new Distance(11);

        //when, then
        assertThatThrownBy(() -> originDistance.decreaseDistance(newDistance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.INVALID_DISTANCE.getMessage());
    }

}