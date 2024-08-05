package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.line.application.LineSectionService;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    private LineSectionService lineSectionService;

    @Test
    void addSection() {
        // given
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red", new LineSections()));
        Station 신사역 = stationRepository.save(new Station("신사역"));
        Station 논현역 = stationRepository.save(new Station("논현역"));
        Station 신논현역 = stationRepository.save(new Station("신논현역"));
        lineSectionService.saveSection(신분당선.getId(),
            new SectionRequest(신사역.getId(), 논현역.getId(), 10L));

        // when
        lineSectionService.saveSection(신분당선.getId(),
            new SectionRequest(논현역.getId(), 신논현역.getId(), 10L));

        // then
        LineResponse lineResponse = lineService.findLine(신분당선.getId());
        assertThat(lineResponse.getStations().stream()
            .map(StationResponse::getName))
            .contains("신사역", "논현역", "신논현역");
    }
}
