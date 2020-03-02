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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void 지하철역_삭제가_된다() throws Exception {
        //given
        StationRequestView requestView = new StationRequestView(1L, "사당역");
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(requestView.toStation()));

        //when
        stationService.delete(requestView);

        //then
        verify(stationRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void 등록되지_않은_지하철역은_삭제할_수_없다() throws Exception {
        //given
        StationRequestView requestView = new StationRequestView(1L, "사당");

        //when, then
        verify(stationRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void 등록된_지하철역_목록을_불러온다() {
        //given
        int theNumberOfStations = 3;
        Station station1 = Station.builder()
                .id(1L)
                .name("사당")
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .name("방배")
                .build();
        Station station3 = Station.builder()
                .id(3L)
                .name("서초")
                .build();
        given(stationRepository.findAll()).willReturn(Arrays.asList(station1, station2, station3));

        //when
        List<Station> stations = stationService.showAll();

        //then
        assertThat(stations.size()).isEqualTo(theNumberOfStations);
        assertThat(stations.get(2).getName()).isEqualTo(station3.getName());
    }

    @Test
    void 등록된_지하철이_목록_없으면_빈_컬렉션을_리턴한다() {
        //when
        List<Station> stations = stationService.showAll();

        //then
        assertThat(stations).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    void 지하철역을_한_개를_조회한다() {
        //given
        StationRequestView requestView = new StationRequestView(1L, "사당");
        Station station = requestView.toStation();
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(station));

        //when
        Station stationById = stationService.findById(requestView);

        //then
        verify(stationRepository, times(1)).findById(anyLong());
        assertThat(stationById.getName()).isEqualTo(station.getName());
    }

    @Test
    void 등록되지_않은_지하철역을_조회하면_익셉션을_발생시킨다() {
        //when, then
        assertThrows(NoSuchElementException.class, () -> {
            stationService.findById(new StationRequestView());
        });
    }
}