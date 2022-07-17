package nextstep.subway.unit;

import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTest {

    @Test
    void 역을_생성한다() {
        // given
        Station station = new Station("암사역");

        // then
        assertThat(station.getName()).isEqualTo("암사역");
    }
}
