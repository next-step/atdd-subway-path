package nextstep.subway.unit;

import io.restassured.RestAssured;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.domain.*;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private DatabaseCleanup databaseCleanup;


    @BeforeEach
    public void setUp() {
        databaseCleanup.execute();
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Long 노선_ID = 1L;
        
        Long 강남_ID = 1L;
        Long 신도림_ID = 2L;
        Long 사당_ID = 3L;
        Long 대림_ID = 4L;
        Long 신대방_ID = 5L;

        stationRepository.save(new Station( "강남역"));
        stationRepository.save(new Station( "신도림역"));
        stationRepository.save(new Station( "사당역"));
        stationRepository.save(new Station( "대림"));
        stationRepository.save(new Station( "신대방역"));

        LineRequest lineRequest = new LineRequest("2호선", "green", 강남_ID, 사당_ID, 30);
        lineService.saveLine(lineRequest);

        SectionRequest newSectionRequest = new SectionRequest(사당_ID, 신도림_ID, 50);
        SectionRequest newSectionRequest2 = new SectionRequest(강남_ID, 신대방_ID, 20);
        SectionRequest newSectionRequest3 = new SectionRequest(신도림_ID, 대림_ID, 10);
        // when
        lineService.addSection(노선_ID, newSectionRequest);
        lineService.addSection(노선_ID, newSectionRequest2);
        lineService.addSection(노선_ID, newSectionRequest3);
        LineResponse lineResponse = lineService.findById(노선_ID);

        System.out.println(lineResponse.toString());
        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(lineResponse.getStations()).hasSize(5)
        );
    }

    @Test
    void showLines() {
        // given
        지하철_라인_역_샘플_저장();

        // when
        List<LineResponse> list = lineService.showLines();

        // then
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void findById() {
        // given
        Line line = 지하철_라인_역_샘플_저장();

        // when
        LineResponse lineResponse = lineService.findById(line.getId());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getStations()).hasSize(2)
        );
    }

    private Line 지하철_라인_역_샘플_저장() {
        Station upStation = new Station("강남역");
        Station downStation = new Station( "사당역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);

        Line line = new Line("2호선", "green");
        line.addSection(upStation, downStation, 30);
        lineRepository.save(line);

        return line;
    }
}
