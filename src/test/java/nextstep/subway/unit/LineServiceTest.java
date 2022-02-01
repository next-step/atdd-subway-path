package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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

    @Test
    void addSection() {
        // given
        final Station upStation = new Station("upStation");
        final Station downStation = new Station("downStation");
        final Station extraStation = new Station("extraStation");
        final Line line = new Line("name", "color");
        line.addSection(new Section(line, upStation, downStation, 10));

        stationRepository.save(upStation);
        stationRepository.save(downStation);
        stationRepository.save(extraStation);
        lineRepository.save(line);

        // when
        lineService.addSection(line.getId(), new SectionRequest(downStation.getId(), extraStation.getId(), 1));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, extraStation);
    }
}
