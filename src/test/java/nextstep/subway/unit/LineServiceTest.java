package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

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
        Station 지하철역 = stationRepository.save(new Station("지하철역"));
        Station 새로운_지하철역 = stationRepository.save(new Station("새로운_지하철역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(지하철역.getId(), 새로운_지하철역.getId(), 10));

        // then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }

    @Test
    void deleteSection() {
        // given
        Station 지하철역 = stationRepository.save(new Station("지하철역"));
        Station 새로운_지하철역 = stationRepository.save(new Station("새로운_지하철역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineService.addSection(신분당선.getId(), new SectionRequest(지하철역.getId(), 새로운_지하철역.getId(), 10));

        // when
        lineService.deleteSection(신분당선.getId(), 새로운_지하철역.getId());

        // then
        assertThat(신분당선.getSections().size()).isEqualTo(0);
    }
}
