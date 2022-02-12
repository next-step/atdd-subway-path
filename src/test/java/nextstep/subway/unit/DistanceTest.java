package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceTest {
	/**
	 * When 지하철 노선 중간 구간 길이가 초과하는 구간을 추가하면
	 * Then 구간 추가가 실패한다.
	 */
	@DisplayName("지하철 노선 중간에 잘못된 거리의 구간을 추가")
	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	void addWrongDistanceSection(int value) {
		// when & then
		assertThatThrownBy(() -> Distance.from(value)).isInstanceOf(IllegalArgumentException.class);
	}
}
