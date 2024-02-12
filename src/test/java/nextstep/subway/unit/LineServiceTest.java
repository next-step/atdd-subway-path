package nextstep.subway.unit;

import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
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
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 강남역;
    private Station 역삼역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        이호선 = new Line("2호선", "green", 10, 강남역, 역삼역);
    }

    @Test
    void addSection() {
        // given
        stationRepository.saveAll(List.of(강남역, 역삼역));
        lineRepository.save(이호선);

        SectionRequest request = new SectionRequest(역삼역.getId(), 강남역.getId(), 10);

        // when
        lineService.createSection(이호선.getId(), request);

        // then
        assertThat(이호선.getSections().getSections()).hasSize(1);
    }
}
