package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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

    private Line 신분당선;

    private Station 강남역;
    private Station 양재역;

    private Section 강남_양재_구간;

    private int distance = 10;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "bg-red-900");
        distance = 10;

        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        강남_양재_구간 = new Section(신분당선, 강남역, 양재역, distance);
    }

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        강남역 = stationRepository.save(강남역);
        양재역 = stationRepository.save(양재역);
        Line line = lineRepository.save(신분당선);
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), distance);

        // when
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        신분당선 = lineRepository.findById(line.getId()).orElseThrow();
        assertThat(line.getSections()).containsExactlyElementsOf(신분당선.getSections());
    }
}
