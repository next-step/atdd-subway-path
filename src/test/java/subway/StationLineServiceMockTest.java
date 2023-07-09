package subway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.domain.*;
import subway.exception.EntityNotFoundException;
import subway.service.StationLineService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static utils.UnitTestUtils.createEntityTestId;
import static utils.UnitTestUtils.createEntityTestIds;

@ExtendWith(MockitoExtension.class)
public class StationLineServiceMockTest {
    @InjectMocks
    private StationLineService stationLineService;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StationLineRepository stationLineRepository;

    @DisplayName("정상적인 지하철역 노선에 구간 추가")
    @Test
    void createStationLineSection() {
        //given
        final Station aStation = new Station("A역");
        final Station bStation = new Station("B역");
        final Station cStation = new Station("C역");

        createEntityTestIds(List.of(aStation, bStation, cStation), 1L);

        given(stationRepository.findById(bStation.getId())).willReturn(Optional.of(bStation));
        given(stationRepository.findById(cStation.getId())).willReturn(Optional.of(cStation));

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        given(stationLineRepository.findById(line.getLineId())).willReturn(Optional.of(line));

        //when & then
        final StationLineSectionCreateRequest request = new StationLineSectionCreateRequest();
        request.setUpStationId(bStation.getId());
        request.setDownStationId(cStation.getId());
        request.setDistance(BigDecimal.TEN);

        Assertions.assertDoesNotThrow(() -> stationLineService.createStationLineSection(line.getLineId(), request));

        then(stationRepository).should(times(1))
                .findById(bStation.getId());
        then(stationRepository).should(times(1))
                .findById(cStation.getId());
        then(stationLineRepository).should(times(1))
                .findById(line.getLineId());
    }

    @DisplayName("존재하지 않는 지하철역을 포함한 구간 생성 요청")
    @Test
    void createStationLineSection_Not_Existing_Station() {
        //given
        final Station aStation = new Station("A역");
        final Station bStation = new Station("B역");
        final Station cStation = new Station("C역");

        createEntityTestIds(List.of(aStation, bStation, cStation), 1L);

        given(stationRepository.findById(bStation.getId())).willReturn(Optional.of(bStation));
        given(stationRepository.findById(cStation.getId())).willReturn(Optional.empty());

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when & then
        final StationLineSectionCreateRequest request = new StationLineSectionCreateRequest();
        request.setUpStationId(bStation.getId());
        request.setDownStationId(cStation.getId());
        request.setDistance(BigDecimal.TEN);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stationLineService.createStationLineSection(line.getLineId(), request));

        then(stationRepository).should(times(1))
                .findById(bStation.getId());
        then(stationRepository).should(times(1))
                .findById(cStation.getId());
        then(stationLineRepository).should(never())
                .findById(line.getLineId());
    }

    @DisplayName("존재하지 않는 지하철역을 포함한 구간 삭제")
    @Test
    void deleteStationLineSection() {
        //given
        final Station aStation = new Station("A역");
        final Station bStation = new Station("B역");
        final Station cStation = new Station("C역");

        createEntityTestIds(List.of(aStation, bStation, cStation), 1L);

        given(stationRepository.findById(cStation.getId())).willReturn(Optional.empty());

        final StationLine line = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.TEN)
                .build();

        line.createSection(bStation, cStation, BigDecimal.TEN);

        createEntityTestId(line, 1L);
        createEntityTestIds(line.getSections(), 1L);

        //when & then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> stationLineService.deleteStationLineSection(line.getLineId(), cStation.getId()));

        then(stationRepository).should(times(1))
                .findById(cStation.getId());
        then(stationLineRepository).should(never())
                .findById(line.getLineId());
    }
}
