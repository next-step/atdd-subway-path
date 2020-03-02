package atdd.path.service;

import atdd.path.application.dto.StationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.Station;
import atdd.path.domain.StationRepository;
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
    @InjectMocks
    StationService stationService;

    @Mock
    StationRepository stationRepository;

    @Test
    void 지하철역_등록이_된다() {
        //given
        StationRequestView requestView = new StationRequestView("사당역");
        Station station = new Station("사당역");
        given(stationRepository.save(any())).willReturn(station);

        //when
        StationResponseView responseView = stationService.create(requestView);

        //then
        assertThat(responseView.getName()).isEqualTo(station.getName());
    }

    @Test
    void 지하철역_삭제가_된다() {
        //given
        StationRequestView requestView = new StationRequestView("사당역");
        Station station = new Station(1L, "사당역");
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(station));

        //when
        stationService.delete(requestView);

        //then
        verify(stationRepository, times(1)).deleteById(anyLong());
    }
}
