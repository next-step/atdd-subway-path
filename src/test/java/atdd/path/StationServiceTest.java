package atdd.path;

import atdd.domain.Station;
import atdd.domain.repository.StationRepository;
import atdd.service.StationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
    @InjectMocks
    StationService stationService;

    @Mock
    StationRepository stationRepository;

    @Test
    void createStation(){
        //given
        Station station = Station.builder()
                .name("강남")
                .build();

        //when
        Station savedStation = stationService.create(station);

        //then
        assertThat(savedStation.getId()).isEqualTo(1L);
        assertThat(savedStation.getName()).isEqualTo("강남");
    }
}
