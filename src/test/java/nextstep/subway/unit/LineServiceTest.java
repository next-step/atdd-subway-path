package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private LineService target;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Test
    void addSection() {
        // given
        final Line line = new Line("name", "color");
        final Line savedLine = lineRepository.save(line);

        final Station savedUpStation = stationRepository.save(new Station("station"));
        final Station savedDownStation = stationRepository.save(new Station("station"));

        // when
        target.addSection(savedLine.getId(), new SectionRequest(savedUpStation.getId(), savedDownStation.getId(), 10));

        // then
        assertThat(savedLine.getSections()).isNotEmpty();
    }
}
