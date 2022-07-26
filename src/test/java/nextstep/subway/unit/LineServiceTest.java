package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
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
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    Line line;
    Station firstStation;
    Station secondStation;
    Station thirdStation;

    @BeforeEach
    void setUp() {
        line = lineRepository.save(new Line("2호선", "red"));
        firstStation = stationRepository.save(new Station("강남역"));
        secondStation = stationRepository.save(new Station("역삼역"));
        thirdStation = stationRepository.save(new Station("선릉역"));
    }

    @Test
    void addSection() {
        addSection(line, firstStation, secondStation, 10);

        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void removeSection() {
        addSection(line, firstStation, secondStation, 10);
        addSection(line, secondStation, thirdStation, 10);

        lineService.deleteSection(line.getId(), thirdStation.getId());

        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void removeSection_fail() {
        addSection(line, firstStation, secondStation, 10);
        addSection(line, secondStation, thirdStation, 10);

        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> lineService.deleteSection(line.getId(), secondStation.getId())
                );
    }

    private void addSection(Line line, Station upStation, Station downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);
        lineService.addSection(line.getId(), sectionRequest);
    }
}
