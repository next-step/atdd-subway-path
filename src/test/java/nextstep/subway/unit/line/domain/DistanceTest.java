package nextstep.subway.unit.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.line.domain.Distance;

@DisplayName("Distance 단위 테스트")
public class DistanceTest {
    @ValueSource(ints = {1, 10, 100, 1000})
    @DisplayName("생성자 테스트")
    @ParameterizedTest
    void ctor(int value) {
        assertThat(new Distance(value).getValue()).isEqualTo(value);
    }

    @ValueSource(ints = {0, -1, -10, -100, -1000})
    @DisplayName("생성자 유효성 검사 실패 테스트")
    @ParameterizedTest
    void ctorFail(int value) {
        assertThatThrownBy(() -> new Distance(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("계산시에도 유효성 검사 진행")
    @Test
    void calculateFail() {
        assertThatThrownBy(() -> new Distance(10).subtraction(new Distance(100))).isInstanceOf(
            IllegalArgumentException.class);
        assertThatThrownBy(() -> new Distance(10).addition(new Distance(-100))).isInstanceOf(
            IllegalArgumentException.class);
    }

    @CsvSource({"10,5,15", "2,1,3", "200,100,300",})
    @DisplayName("더하기 테스트")
    @ParameterizedTest
    void addition(int leftDistance, int rightDistance, int result) {
        Distance distance = new Distance(leftDistance);
        Distance thatDistance = new Distance(rightDistance);

        assertThat(distance.addition(thatDistance).getValue()).isEqualTo(result);
    }

    @CsvSource({"10,5,5", "2,1,1", "200,100,100"})
    @DisplayName("빼기 테스트")
    @ParameterizedTest
    void subtraction(int leftDistance, int rightDistance, int result) {
        Distance distance = new Distance(leftDistance);
        Distance thatDistance = new Distance(rightDistance);

        assertThat(distance.subtraction(thatDistance).getValue()).isEqualTo(result);
    }

    @CsvSource(value = {"10,5,true", "3,2,true", "200,100,true", "100,100,false", "1,3,false",
        "100,200,false"}, delimiter = ',')
    @DisplayName("부등호 테스트 - 초과")
    @ParameterizedTest
    void greaterThan(int leftDistance, int rightDistance, boolean result) {
        Distance distance = new Distance(leftDistance);
        Distance thatDistance = new Distance(rightDistance);

        assertThat(distance.greaterThan(thatDistance)).isEqualTo(result);
    }

    @CsvSource(value = {"10,10,true", "10,5,false"}, delimiter = ',')
    @DisplayName("Equals 테스트")
    @ParameterizedTest
    void equals(int leftDistance, int rightDistance, boolean result) {
        Distance distance = new Distance(leftDistance);
        Distance thatDistance = new Distance(rightDistance);

        assertThat(distance.equals(thatDistance)).isEqualTo(result);
    }
}
