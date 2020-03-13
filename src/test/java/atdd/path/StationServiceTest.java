package atdd.path;

import atdd.domain.Station;
import atdd.domain.repository.StationRepository;
import atdd.service.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
    private Station station = Station.builder()
            .name("강남")
            .build();

    @InjectMocks
    StationService stationService;

    @Mock
    StationRepository stationRepository;

    @Test
    void createStation() {
        //given
        given(stationRepository.save(any(Station.class))).willReturn(station);

        //when
        Station savedStation = stationService.create(station);

        //then
        verify(stationRepository, times(1)).save(any(Station.class));
        assertThat(savedStation.getName()).isEqualTo("강남");
    }

    @Test
    void deleteStation() {
        //given
        given(stationRepository.findById(1L)).willReturn(Optional.of(station));

        //when
        stationService.delete(1L);

        //then
        verify(stationRepository, times(1)).deleteById(anyLong());
    }
}
