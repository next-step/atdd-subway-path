package nextstep.subway.station.application;

import com.google.common.collect.Lists;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StationServiceMockTest {
    @Mock
    private StationRepository stationRepository;

    @Autowired
    private StationService stationService;
    private StationRequest 강남역;
    private StationRequest 역삼역;
    private StationRequest 삼성역;

    @BeforeEach
    void setUp() {
        stationService = new StationService(stationRepository);

        강남역 = new StationRequest("강남역");
        역삼역 = new StationRequest("역삼역");
        삼성역 = new StationRequest("삼성역");
    }

    @Test
    void saveStation() {
        // given
        when(stationRepository.save(any())).thenReturn(강남역.toStation());

        // when
        StationResponse 저장된_강남역 = stationService.saveStation(강남역);

        // then
        assertThat(저장된_강남역).isNotNull();
        assertThat(저장된_강남역.getName()).isEqualTo(강남역.getName());
    }

    @Test
    void findAllStations() {
        // given
        when(stationRepository.findAll()).thenReturn(Lists.newArrayList(new Station()));

        // when
        List<StationResponse> 전체역들 = stationService.findAllStations();

        // then
        assertThat(전체역들).isNotEmpty();
    }

    @Test
    void deleteStationById() {
        // when / then
        assertThatCode(() ->
            stationService.deleteStationById(1L)
        ).doesNotThrowAnyException();
        verify(stationRepository).deleteById(anyLong());
    }

    @Test
    void findStationById() {
        // given
        when(stationRepository.findById(any())).thenReturn(Optional.ofNullable(new Station("강남역")));

        // when
        Station 조회된_강남역 = stationService.findStationById(1L);

        // then
        assertThat(조회된_강남역).isEqualTo(강남역.toStation());
    }

    @DisplayName("예외처리 - 없는 역 조회시 예외발생")
    @Test
    void findStationByIdException() {
        // given
        Long 없는역_Id = 9999L;
        when(stationRepository.findById(없는역_Id)).thenThrow(IllegalArgumentException.class);

        // when / then
        assertThatThrownBy(() -> {
            stationService.findStationById(없는역_Id);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
