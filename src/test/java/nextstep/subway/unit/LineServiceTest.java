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
        Station gangnam = new Station("강남역");
        Station yeoksam = new Station("역삼역");
        stationRepository.save(gangnam);
        stationRepository.save(yeoksam);

        Line line = new Line("2호선", "bg-green-600");
        lineRepository.save(line);

        // when
        SectionRequest sectionRequest = new SectionRequest(gangnam.getId(), yeoksam.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        assertThat(line.getSections()).containsOnly(new Section(line, gangnam, yeoksam, 10));
    }
}
