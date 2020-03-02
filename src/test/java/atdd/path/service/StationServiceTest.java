package atdd.path.service;

import atdd.path.application.dto.CreateStationRequestView;
import atdd.path.domain.Station;
import atdd.path.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
    @InjectMocks
    StationService stationService;

    @MockBean
    StationRepository stationRepository;

    @Test
    void 지하철역_등록이_된다(){
        //given
        CreateStationRequestView requestView
                = new CreateStationRequestView("사당역");
        given(stationRepository.save(any(Station.class))).willReturn(requestView.toStation());

        //when
        stationService.create(requestView);

        //then
        verify(stationRepository).save(any());
    }

}
