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
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Long lineId = 1L;
        Station 강남역 = new Station(1L, "강남역");
        Station 양재역 = new Station(2L, "양재역");
        Line line = new Line("신분당선", "빨강");

        lineRepository.save(line);
        stationRepository.save(강남역);
        stationRepository.save(양재역);

        // when
        lineService.addSection(lineId, new SectionRequest(1L, 2L, 10));

        // then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("양재역");
    }
}
