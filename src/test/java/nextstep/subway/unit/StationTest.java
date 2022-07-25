package nextstep.subway.unit;

import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 테스트")
public class StationTest {

    @Test
    @DisplayName("역을 생성한다.")
    void createStation() {
        Station 강남역 = new Station(1L, "강남역");

        assertThat(강남역.getName()).isEqualTo("강남역");
    }

}
