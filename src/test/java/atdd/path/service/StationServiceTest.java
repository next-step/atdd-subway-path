package atdd.path.service;

import atdd.path.application.dto.CreateStationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.Station;
import atdd.path.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
    @InjectMocks
    StationService stationService;

    @Mock
    StationRepository stationRepository;

    @Test
    void 지하철역_등록이_된다() {
        //given
        CreateStationRequestView requestView
                = new CreateStationRequestView("사당역");
        Station station = new Station("사당역");
        given(stationRepository.save(any())).willReturn(station);

        //when
        StationResponseView responseView = stationService.create(requestView);

        //then
        assertThat(responseView.getName()).isEqualTo(station.getName());
    }
}
