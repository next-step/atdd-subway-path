package nextstep.subway.unit;

import nextstep.subway.applicaion.command.LineCommandService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.query.LineQueryService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineCommandService lineCommandService;
    @Autowired
    private LineQueryService lineQueryService;

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
    }

    @Test
    void addSection() {
        // given
        신분당선 = Line.of("신분당선", "red", 강남역, 판교역, 10);
        stationRepository.save(강남역);
        stationRepository.save(판교역);
        stationRepository.save(정자역);
        lineRepository.save(신분당선);
        SectionRequest sectionRequest = SectionRequest.of(판교역.getId(), 정자역.getId(), 10);

        // when
        lineCommandService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line line = lineQueryService.findLineById(신분당선.getId());
        assertThat(line.getId()).isEqualTo(신분당선.getId());
        assertThat(line.getStations().size()).isEqualTo(3);
    }

}
