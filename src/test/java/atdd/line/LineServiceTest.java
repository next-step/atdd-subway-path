package atdd.line;


import atdd.Edge.Edge;
import atdd.Edge.EdgeRepository;
import atdd.station.Station;
import atdd.station.StationRepository;
import atdd.station.StationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LineServiceTest {

    @Autowired
    LineService lineService;
    @Autowired
    EdgeRepository edgeRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    LineRepository lineRepository;

    @AfterEach
    public void cleanUp(){
        edgeRepository.deleteAll();
        stationRepository.deleteAll();
        lineRepository.deleteAll();

    }

    @DisplayName("지하철 노선 정보를 조회한다")
    @Test
    public void findLineById(){
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

         Station resultStationA = stationRepository.save(stationA);
         Station resultStationB = stationRepository.save(stationB);
         Station resultStationC = stationRepository.save(stationC);

        //노선 등록
        String LINE_NAME = "2호선";
        LocalTime START_TIME = LocalTime.of(5,0);
        LocalTime END_TIME = LocalTime.of(23, 30);
        int INTERVAL_TIME = 10;
        int EXTRA_FARE = 0;

        LineCreateRequest lineResponse = LineCreateRequest.builder()
                .name(LINE_NAME)
                .start_time(START_TIME)
                .end_time(END_TIME)
                .interval_time(INTERVAL_TIME)
                .extra_fare(EXTRA_FARE)
                .build();


        LineResponse createResult = lineService.createLine(lineResponse);

         //구간 등록
        Edge edgeA = Edge.builder()
                .lineId(createResult.getId())
                .elapsedTime(3)
                .distance(new BigDecimal("1.1"))
                .sourceStationId(resultStationA.getId())
                .targetStationId(resultStationB.getId())
                .build();
        Edge edgeB = Edge.builder()
                .lineId(createResult.getId())
                .elapsedTime(3)
                .distance(new BigDecimal("1.3"))
                .sourceStationId(resultStationB.getId())
                .targetStationId(resultStationC.getId())
                .build();

        edgeRepository.save(edgeA);
        edgeRepository.save(edgeB);

        LineDetailResponse lineDetailResponse = lineService.findLineByIdWithStationList(createResult.getId());

        assertThat(lineDetailResponse.getLine().getName()).isEqualTo(LINE_NAME);
        assertThat(lineDetailResponse.getStations().size()).isEqualTo(3);

    }

}