package atdd.path;

import atdd.domain.Station;
import atdd.domain.repository.StationRepository;
import atdd.service.StationService;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    void 지하철역_등록하기() {
        //given
        given(stationRepository.save(any(Station.class))).willReturn(station);

        //when
        Station savedStation = stationService.create(station);

        //then
        verify(stationRepository, times(1)).save(any(Station.class));
        assertThat(savedStation.getName()).isEqualTo("강남");
    }

    @Test
    void 지하철역_삭제하기() {
        //when
        stationService.delete(1L);

        //then
        verify(stationRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void 등록된_지하철역만_삭제가능하다(){
        //given
        given(stationRepository.findById(1L)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("등록된 지하철역만 삭제 가능합니다.");
    }

    @Test
    void 지하철역_조회하기(){
        //given
        given(stationRepository.findById(1L)).willReturn(Optional.of(station));

        //when
        Station stationById = stationService.findById(1L);

        //then
        assertThat(stationById.getName()).isEqualTo(station.getName());
        verify(stationRepository, times(1)).findById(1L);
    }
}
