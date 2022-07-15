package nextstep.subway.applicaion.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LineRequestTest {

    @ParameterizedTest
    @CsvSource({"1,1,1"})
    void hasSectionData_True(final Long upStationId, final Long downStationId, final int distance) {
        // given
        final LineRequest request = createLineRequest(upStationId, downStationId, distance);

        // when
        final boolean result = request.hasSectionData();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({",,1", ",1,0", "1,,0", "1,1,-1"})
    void hasSectionData_False(final Long upStationId, final Long downStationId, final int distance) {
        // given
        final LineRequest request = createLineRequest(upStationId, downStationId, distance);

        // when
        final boolean result = request.hasSectionData();

        // then
        assertThat(result).isFalse();
    }

    private LineRequest createLineRequest(final Long upStationId, final Long downStationId, final int distance) {
        return new LineRequest("name", "color", upStationId, downStationId, distance);
    }

}