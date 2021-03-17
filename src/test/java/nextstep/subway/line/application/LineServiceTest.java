package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station station1;
    private Station station2;
    private Station station3;
    private Line line;

    @BeforeEach
    public void setUp(){
        station1 = stationRepository.save(new Station("강남역"));
        station2 = stationRepository.save(new Station("양재역"));
        station3 = stationRepository.save(new Station("판교역"));
        line = lineRepository.save(new Line("신분당선", "red"));
    }

    @DisplayName("지하철 노선에 처음으로 구간을 등록한다.")
    @Test
    void addSection() {
        //When
        lineService.addSection(line.getId(), new SectionRequest(station1.getId(), station2.getId(), 5));

        //Then
        Line expected = lineService.findLineById(line.getId());

        assertAll(
                () -> assertThat(expected.getSections()).hasSize(1),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(station1, station2))
        );
    }

    @DisplayName("새로운 구간을 추가한다. 추가된 상행역은 기존의 하행역과 동일하다.")
    @Test
    public void addSection_case2(){
        //Given
        lineService.addSection(line.getId(), new SectionRequest(station1.getId(), station2.getId(), 10));

        //When
        lineService.addSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 5));

        //Then
        Line expected = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(expected.getSections()).hasSize(2),
                () -> assertThat(expected.getStations()).containsExactlyElementsOf(Arrays.asList(station1, station2, station3))
        );
    }

    @DisplayName("새로운 구간을 추가한다. 새로운 역은 상행 종점으로 등록된다.")
    @Test
    public void addSection_case3(){
        //Given
        lineService.addSection(line.getId(), new SectionRequest(station1.getId(), station2.getId(), 10));

        //When
        lineService.addSection(line.getId(), new SectionRequest(station3.getId(), station1.getId(), 5));

        //Then
        Line expected = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(expected.getSections()).hasSize(2)
        );
    }
}
