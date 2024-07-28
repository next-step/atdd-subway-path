package nextstep.subway.unit;

import nextstep.subway.Station;
import nextstep.subway.StationRepository;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineSection;
import nextstep.subway.line.LineSectionRepository;
import nextstep.subway.line.LineSections;
import nextstep.subway.line.LineService;
import nextstep.subway.line.dto.LineSectionAppendRequest;
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
    private LineService lineService;

    @Autowired
    private LineSectionRepository lineSectionRepository;


    @Test
    void addSection() {
        // given
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("양재역"));
        Line line = lineRepository.save(new Line(null, "신분당선", "RED", new LineSections()));
        LineSection lineSection = new LineSection(line, station1, station2, 10);
        // when
        lineService.appendLineSection(line.getId(), new LineSectionAppendRequest(station1.getId(), station2.getId(), 10));
        // then
        assertThat(line.getSections()).hasSize(1);
    }
}
