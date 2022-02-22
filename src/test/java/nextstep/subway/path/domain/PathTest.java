package nextstep.subway.path.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nextstep.subway.path.exception.PathInvalidDistanceException;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PathTest {

    @DisplayName("경로가 비어있다면, PathNotFoundException 이 발생한다.")
    @Test
    void emptyPath() {
        // given
        List<Station> stations = Collections.emptyList();
        int distance = 1;

        // when, then
        assertThatExceptionOfType(PathNotFoundException.class)
                .isThrownBy(() -> new Path(stations, distance));
    }

    @DisplayName("경로의 길이가 음수라면, PathInvalidDistanceException 이 발생한다.")
    @ValueSource(ints = {0, -1, -11, -111})
    @ParameterizedTest
    void negativePath(int distance) {
        // given
        List<Station> stations = Arrays.asList(
                new Station("강남역"),
                new Station("역삼역"),
                new Station("선릉역")
        );

        // when, then
        assertThatExceptionOfType(PathInvalidDistanceException.class)
                .isThrownBy(() -> new Path(stations, distance));
    }
}
