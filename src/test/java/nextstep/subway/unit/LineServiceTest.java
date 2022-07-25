package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
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
    private LineService lineService;

    @Test
    void addSection() {
        // given
        final Station 기흥역 = new Station("기흥역");
        stationRepository.save(기흥역);
        final Station 신갈역 = new Station("신갈역");
        stationRepository.save(신갈역);
        final Section section = Section.builder()
                                       .upStation(기흥역)
                                       .downStation(신갈역)
                                       .distance(10).build();
        final Line line = new Line("분당선", "yellow");
        lineRepository.save(line);

        // when
        lineService.addSection(line.getId(), new SectionRequest(section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance()));

        // then
        assertThat(line.getSections()).containsExactlyInAnyOrder(section);
    }
}
