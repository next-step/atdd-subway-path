package atdd.path;

import atdd.domain.Station;
import atdd.domain.repository.StationRepository;
import atdd.service.StationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        given(stationRepository.save(any(Station.class))).willReturn(station);

        //when
        Station savedStation = stationService.create(station);

        //then
        verify(stationRepository, times(1)).save(any(Station.class));
        assertThat(savedStation.getName()).isEqualTo("강남");
    }
}
