package atdd.station;

import atdd.Edge.Edge;
import atdd.Edge.EdgeRepository;
import atdd.line.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StationServiceTest {
    @Autowired
    StationService stationService;
    @Autowired
    EdgeRepository edgeRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    LineService lineService;
    @Autowired
    LineRepository lineRepository;

    @AfterEach
    public void cleanUp(){
        edgeRepository.deleteAll();
        stationRepository.deleteAll();
        lineRepository.deleteAll();
    }

    @DisplayName("지하철역 상세 정보에 노선정보 포함 하는가")
    @Test
    void findLines() {
        //지하철역 등록
        Station stationA = Station.builder()
                .name("강남역")
                .build();

        Station stationB = Station.builder()
                .name("교대역")
                .build();
        Station stationC = Station.builder()
                .name("서초역")
                .build();
        Station stationD = Station.builder()
                .name("양재역")
                .build();

        Station resultStationA = stationRepository.save(stationA);
        Station resultStationB = stationRepository.save(stationB);
        Station resultStationC = stationRepository.save(stationC);
        Station resultStationD = stationRepository.save(stationD);


        //노선 등록
        LocalTime START_TIME = LocalTime.of(5,0);
        LocalTime END_TIME = LocalTime.of(23, 30);
        int INTERVAL_TIME = 10;
        int EXTRA_FARE = 0;

        Line lineA = Line.builder()
                .name("2호선")
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();

        Line lineB = Line.builder()
                .name("신분당선")
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();

        Line resultLineA = lineRepository.save(lineA);
        Line resultLineB = lineRepository.save(lineB);

        //구간 등록
        Edge edgeA = Edge.builder()
                .lineId(resultLineA.getId())
                .elapsedTime(3)
                .distance(new BigDecimal("1.1"))
                .sourceStationId(resultStationA.getId())
                .targetStationId(resultStationB.getId())
                .build();
        Edge edgeB =  Edge.builder()
                .lineId(resultLineA.getId())
                .elapsedTime(3)
                .distance(new BigDecimal("1.1"))
                .sourceStationId(resultStationB.getId())
                .targetStationId(resultStationC.getId())
                .build();

        Edge edgeC = Edge.builder()
                .lineId(resultLineB.getId())
                .elapsedTime(3)
                .distance(new BigDecimal("1.3"))
                .sourceStationId(resultStationA.getId())
                .targetStationId(resultStationD.getId())
                .build();

        edgeRepository.save(edgeA);
        edgeRepository.save(edgeB);
        edgeRepository.save(edgeC);

        StationDetailResponse stationDetailResponse = stationService.findByIdWithLineList(1L);

        assertThat(stationDetailResponse.getLines().size()).isEqualTo(2);
    }
}