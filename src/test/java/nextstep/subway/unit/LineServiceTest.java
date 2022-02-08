package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.SectionRepository;
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
    private StationService stationService;
    @Autowired
    private LineService lineService;
    @Autowired
    private SectionRepository sectionRepository;

    @Test
    void addSection() {
        // given
        지하철_역_저장();
        LineService lineService = new LineService(lineRepository, stationService, sectionRepository);
        lineService.saveLine(new LineRequest("신분당선", "red", 1L, 2L, 100));

//        // when
        lineService.addSection(1L, new SectionRequest(2L, 3L, 200));

//        // then
        Line 신분당선 = lineRepository.findById(1L).get();
        assertThat(신분당선.getSections().get(0).getDownStation().getName()).isEqualTo("강남");
        assertThat(신분당선.getSections().get(0).getUpStation().getName()).isEqualTo("양재");
        assertThat(신분당선.getSections().get(1).getDownStation().getName()).isEqualTo("양재");
        assertThat(신분당선.getSections().get(1).getUpStation().getName()).isEqualTo("미금");
    }

    public void 지하철_역_저장() {
        stationService.saveStation(new StationRequest("강남"));
        stationService.saveStation(new StationRequest("양재"));
        stationService.saveStation(new StationRequest("미금"));
        stationService.saveStation(new StationRequest("정자"));
        stationService.saveStation(new StationRequest("판교"));
    }
}