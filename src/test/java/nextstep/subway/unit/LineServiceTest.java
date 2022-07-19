package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Station upStation = stationRepository.save(new Station("양재역"));
        Station downStation = stationRepository.save(new Station("교대역"));
        Line line = lineRepository.save(new Line("신분당선", "red"));

        // when
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 6);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getUpStation()).isEqualTo(upStation);
        assertThat(sections.get(0).getDownStation()).isEqualTo(downStation);
    }
}
