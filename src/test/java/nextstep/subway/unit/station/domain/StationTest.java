package nextstep.subway.unit.station.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

@DisplayName("Station 단위 테스트")
public class StationTest {
    private Station station;

    @BeforeEach
    void setUp() {
        this.station = new Station(1L, "지하철 역");
    }

    @DisplayName("ID Match")
    @Test
    void matchId() {
        assertThat(station.matchId(1L)).isTrue();
    }

    @DisplayName("ID로 Equals")
    @Test
    void equals() {
        assertThat(station).isEqualTo(station);
        assertThat(station).isEqualTo(new Station(station.getId(), null));
    }
}
