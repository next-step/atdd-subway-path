package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.StationTest;
import atdd.station.dto.PathResponseDto;
import atdd.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class StationServiceTest {

    private StationService stationService;

    private StationAssembler stationAssembler;
    private StationRepository stationRepository;
    private ShortestPathFinder shortestPathFinder;

    @BeforeEach
    void setup() {
        stationAssembler = new StationAssembler();
        stationRepository = mock(StationRepository.class);
        shortestPathFinder = mock(ShortestPathFinder.class);
        stationService = new StationService(stationAssembler, stationRepository, shortestPathFinder);
    }

    @DisplayName("create - name 이 null 이거나 비어있으면 에러")
    @ParameterizedTest
    @NullAndEmptySource
    void create(String name) {
        assertThatThrownBy(() -> stationService.create(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name 은 필수 입니다.");
    }

    @DisplayName("조회 결과가 없으면 에러")
    @Test
    void getStation() {
        final Long id = 5145L;

        when(stationRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stationService.getStation(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 stationId 입니다. stationId : [5145]");
    }

    @DisplayName("delete - 삭제대상이 없으면 에러")
    @Test
    void delete_no_result() {
        final Long id = 13247L;

        when(stationRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stationService.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 stationId 입니다. stationId : [13247]");
    }

    @Test
    void getShortestPath() throws Exception {
        final Station startStation = StationTest.create(111L, "startStation!!");
        final Station endStation = StationTest.create(222L, "endStation!!");
        final PathResponseDto responseDto = PathResponseDto.of(startStation.getId(), endStation.getId(), Collections.emptyList());

        given(stationRepository.findById(startStation.getId())).willReturn(Optional.of(startStation));
        given(stationRepository.findById(endStation.getId())).willReturn(Optional.of(endStation));
        given(shortestPathFinder.findPath(startStation, endStation)).willReturn(responseDto);


        final PathResponseDto result = stationService.getShortestPath(startStation.getId(), endStation.getId());


        assertThat(result.getStartStationId()).isEqualTo(startStation.getId());
        assertThat(result.getEndStationId()).isEqualTo(endStation.getId());

        verify(stationRepository, times(1)).findById(startStation.getId());
        verify(stationRepository, times(1)).findById(endStation.getId());
        verify(shortestPathFinder, times(1)).findPath(startStation, endStation);
    }

}