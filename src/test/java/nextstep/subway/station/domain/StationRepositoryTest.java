package nextstep.subway.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("지하철 역이 존재하지 않을 경우")
    void nonExistentStationName() {
        // given
        String stationName = "강남역";

        // when
        boolean result = stationRepository.existsByName(stationName);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("지하철 역이 존재할 경우")
    void existStationName() {
        // given
        Station savedStation = stationRepository.save(new Station("강남역"));

        // when
        boolean result = stationRepository.existsByName(savedStation.getName());

        // then
        assertThat(result).isTrue();
    }
}
