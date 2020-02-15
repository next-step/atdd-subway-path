package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.dto.StationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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

        final Station station = Station.of(id, name);

        final StationResponseDto responseDto = stationAssembler.convertToDto(station);

        isEqual(responseDto, station);
    }

    private void isEqual(StationResponseDto responseDto, Station station) {
        assertThat(responseDto.getId()).isEqualTo(station.getId());
        assertThat(responseDto.getName()).isEqualTo(station.getName());
    }

    @DisplayName("Station 리스트를 StationResponseDto 리스트로 변환한다.")
    @Test
    void convertToDtos() {

        final Station station1 = Station.of(55134L, "name1111");
        final Station station2 = Station.of(79869L, "name2222");

        final List<Station> stations = Arrays.asList(station1, station2);

        final List<StationResponseDto> result = stationAssembler.convertToDtos(stations);

        assertThat(result).hasSize(stations.size());

        isEqual(result.get(0), station1);
        isEqual(result.get(1), station2);
    }

}