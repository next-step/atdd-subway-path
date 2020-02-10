package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.dto.StationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class StationAssemblerTest {

    private StationAssembler stationAssembler;

    @BeforeEach
    void setup() {
        stationAssembler = new StationAssembler();
    }

    @DisplayName("Station 엔티티를 StationResponseDto 로 변환한다.")
    @Test
    void convertToDto() {
        final Long id = 551342L;
        final String name = "name!!!";

        final Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);

        final StationResponseDto responseDto = stationAssembler.convertToDto(station);

        assertThat(responseDto.getId()).isEqualTo(station.getId());
        assertThat(responseDto.getName()).isEqualTo(station.getName());
    }

}