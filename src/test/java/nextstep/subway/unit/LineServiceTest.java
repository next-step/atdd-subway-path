package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.section.SectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {

    Station 강남역;
    Station 역삼역;
    Station 선릉역;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");

        stationRepository.saveAll(List.of(강남역, 역삼역, 선릉역));
    }

    @Test
    void addSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        lineRepository.save(이호선);

        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 선릉역.getId(), 10L);
        lineService.addSection(이호선.getId(), sectionRequest);

        LineResponse lineResponse = lineService.findLineById(이호선.getId());
        assertThat(lineResponse.getStations()).hasSize(3);
    }

    @Test
    void getStations() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        lineRepository.save(이호선);

        LineResponse lineResponse = lineService.findLineById(이호선.getId());

        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @Test
    void removeSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        lineRepository.save(이호선);

        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 선릉역.getId(), 10L);
        lineService.addSection(이호선.getId(), sectionRequest);
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        LineResponse lineResponse = lineService.findLineById(이호선.getId());
        assertThat(lineResponse.getStations()).hasSize(2);
    }
}
