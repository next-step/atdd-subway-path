package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

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

@SpringBootTest
@Transactional
public class LineServiceTest {
    private Line 신분당선;
    private Station 강남역;
    private Station 양재역;
    private Section section;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");

        강남역 = new Station("강남역");
        양재역 = new Station("양재역");

        section = new Section(신분당선, 강남역, 양재역, 10);

        신분당선.addSection(section);
    }

    @Test
    void addSection() {
        // given
        // 신분당선 : 강남역 - 양재역
        lineRepository.save(신분당선);
        // 양재시민의숲역 추가
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        stationRepository.save(양재시민의숲역);

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 양재시민의숲역.getId(), 20));

        // then
        assertThat(신분당선.getStations()).containsExactly(강남역, 양재역, 양재시민의숲역);
    }
}
