package nextstep.subway.applicaion.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class LineRequestTest {

    @ParameterizedTest(name = "upStationId, downStationId 값이 존재하고 distance 값이 0이 아니면 Section을 만들도록 True를 반환한다.")
    @CsvSource({"1,2,10"})
    void isCreateSectionTest(Long upStationId, Long downStationId, Integer distance) {
        assertThat(new LineRequest("4호선", "blue").isCreateSection()).isFalse();
        assertThat(new LineRequest("4호선", "blue", upStationId, downStationId, distance).isCreateSection()).isTrue();
    }

    @ParameterizedTest(name = "upStationId 또는 downStationId가 null 이거나 distance 가 0 이면 false를 반환한다.")
    @CsvSource({",2,10", "1,,10", "1,2,0",})
    void isCreateSectionWithoutUpStationIdTest(Long upStationId, Long downStationId, Integer distance) {
        assertThat(new LineRequest("4호선", "blue", upStationId, downStationId, distance).isCreateSection()).isFalse();
    }
}