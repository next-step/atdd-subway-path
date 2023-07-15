package subway.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.domain.*;
import subway.service.StationPathService;
import subway.service.dto.StationPathResponse;
import subway.service.dto.StationResponse;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static utils.UnitTestUtils.createEntityTestIds;

@ExtendWith(MockitoExtension.class)
public class StationPathServiceTest {
    @InjectMocks
    private StationPathService stationPathService;

    @Mock
    private StationLineRepository stationLineRepository;

    @Mock
    private StationRepository stationRepository;

    Station aStation, bStation, cStation, dStation, eStation, fStation, gStation, hStation, iStation;

    //A,B,C
    StationLine line_1;
    //C,D,E
    StationLine line_2;
    //E,F,G
    StationLine line_3;
    //G,H,I,A
    StationLine line_4;

    @BeforeEach
    void setUp() {
        //given
        aStation = new Station("A역");
        bStation = new Station("B역");
        cStation = new Station("C역");
        dStation = new Station("D역");
        eStation = new Station("E역");
        fStation = new Station("F역");
        gStation = new Station("G역");
        hStation = new Station("H역");
        iStation = new Station("I역");

        final List<Station> stations = List.of(aStation, bStation, cStation, dStation, eStation, fStation, gStation, hStation, iStation);
        createEntityTestIds(stations, 1L);

        given(stationRepository.findAll()).willReturn(stations);


        line_1 = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(aStation)
                .downStation(bStation)
                .distance(BigDecimal.valueOf(8L))
                .build();

        line_1.createSection(bStation, cStation, BigDecimal.ONE);

        line_2 = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(cStation)
                .downStation(dStation)
                .distance(BigDecimal.valueOf(9L))
                .build();
        line_2.createSection(dStation, eStation, BigDecimal.valueOf(7L));

        line_3 = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(eStation)
                .downStation(fStation)
                .distance(BigDecimal.valueOf(4L))
                .build();
        line_3.createSection(fStation, gStation, BigDecimal.valueOf(3L));

        line_4 = StationLine.builder()
                .name("1호선")
                .color("blue")
                .upStation(gStation)
                .downStation(hStation)
                .distance(BigDecimal.ONE)
                .build();
        line_4.createSection(hStation, iStation, BigDecimal.valueOf(7L));
        line_4.createSection(iStation, aStation, BigDecimal.valueOf(2L));

        final List<StationLine> stationLines = List.of(line_1, line_2, line_3, line_4);
        createEntityTestIds(stationLines, 1L);

        given(stationLineRepository.findAll()).willReturn(stationLines);

        final List<StationLineSection> stationLineSections = stationLines.stream()
                .map(StationLine::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        createEntityTestIds(stationLineSections, 1L);
    }

    @DisplayName("정상적인 지하철 경로 조회")
    @Test
    void searchStationPath() {
        //given
        final Long startStation = aStation.getId();
        final Long destinationStation = eStation.getId();

        //when
        final StationPathResponse response = stationPathService.searchStationPath(startStation, destinationStation);

        //then
        final BigDecimal expectedDistance = BigDecimal.valueOf(17);
        Assertions.assertEquals(0, expectedDistance.compareTo(response.getDistance()));

        final List<String> expectedPathStationNames = List.of("A역", "I역", "H역", "G역", "F역", "E역");
        final List<String> resultPathStationNames = response.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        Assertions.assertArrayEquals(expectedPathStationNames.toArray(), resultPathStationNames.toArray());
    }
}
