package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionOnLineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 신논현역 = stationRepository.save(new Station("신논현역"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));

        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600", new Section(강남역, 양재역, 10)));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(신논현역.getId(), 강남역.getId(), 5);
        lineService.registerSection(신분당선.getId(), sectionRequest);

        // then
        // lineService.findLineById 메서드를 통해 검증
        LineResponse line = lineService.findLine(신분당선.getId());
        List<SectionOnLineResponse> sections = line.getSections();
        assertThat(sections).hasSize(2);
    }
}
