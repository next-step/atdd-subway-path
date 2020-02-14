package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.dto.station.StationCreateRequestDto;
import atdd.station.dto.station.StationCreateResponseDto;
import atdd.station.dto.station.StationDetailResponseDto;
import atdd.station.dto.station.StationListResponseDto;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static atdd.station.controller.StationAcceptanceTest.KANGNAM_STATION_JSON;
import static atdd.station.fixture.StationFixture.*;
import static atdd.station.fixture.SubwaysFixture.SUBWAYS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class StationServiceTest {

    private static final long KANGNAM_STATION_ID = 0L;

    @Mock
    StationRepository stationRepository;

    @InjectMocks
    StationService stationService;

    @Test
    public void 지하철역_생성시_성공하는지(SoftAssertions softly) {
        //given
        Station station = Station.builder()
                .name(KANGNAM_STATION_JSON)
                .subways(SUBWAYS)
                .build();

        //when
        when(stationRepository.save(any())).thenReturn(station);

        StationCreateResponseDto createStation = stationService.create
                (StationCreateRequestDto.toDtoEntity(station.getName(), station.getSubways()));

        //then
        softly.assertThat(createStation.getId()).isEqualTo(KANGNAM_STATION_ID);
        softly.assertThat(createStation.getName()).isEqualTo(KANGNAM_STATION_JSON);
    }

    @Test
    public void 지하철역_list_조회가_성공하는지(SoftAssertions softly) {
        //given
        List<Station> stations = Arrays.asList(KANNAM_STATON, PANGYO);

        //whens
        when(stationRepository.findAll()).thenReturn(stations);

        StationListResponseDto createStations = stationService.list();

        //then
        softly.assertThat(createStations).isNotNull();
        softly.assertThat(createStations.getListDtoSize()).isGreaterThan(1);
        softly.assertThat(createStations.toString()).contains(KANGNAM_STATION_NAME);
    }

    @Test
    public void 지하철역_상세_조회가_성공하는지(SoftAssertions softly) {
        //when
        when(stationRepository.findById(KANGNAM_STATION_ID)).thenReturn(java.util.Optional.of(KANNAM_STATON));

        StationDetailResponseDto station = stationService.detail(KANGNAM_STATION_ID);

        //then
        softly.assertThat(station).isNotNull();
        softly.assertThat(station.getName()).contains(KANGNAM_STATION_NAME);
    }

    @Test
    public void 지하철역_삭제가_성공하는지(SoftAssertions softly) {
        //given
        Station station = KANNAM_STATON;

        //when
        when(stationRepository.findById(KANGNAM_STATION_ID)).thenReturn(java.util.Optional.of(station));
        stationService.delete(KANGNAM_STATION_ID);

        //then
        softly.assertThat(station.isDeleted()).isTrue();
    }
}
