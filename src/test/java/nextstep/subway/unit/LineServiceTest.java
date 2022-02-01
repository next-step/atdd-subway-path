package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    Station 합정역;
    Station 신도림역;
    Station 홍대입구역;
    Line _2호선;

    @BeforeEach
    void setUp() {
        합정역 = stationRepository.save(new Station("합정역"));
        신도림역 = stationRepository.save(new Station("신도림역"));
        홍대입구역 = stationRepository.save(new Station("홍대입구역"));
        _2호선 = new Line("2호선","green");
        _2호선.getSections().add(new Section(_2호선, 합정역, 신도림역, 10));
        lineRepository.save(_2호선);
    }



    @Test
    @DisplayName("Service Layer - 구간 등록 기능")
    void addSection() {
        //given
        SectionRequest sectionRequest = new SectionRequest(신도림역.getId(), 홍대입구역.getId(), 10);

        // when
        lineService.addSection(_2호선.getId(), sectionRequest);

        // then
        List<Station> stations = _2호선.getStations();
        assertThat(stations).containsExactly(합정역, 신도림역, 홍대입구역);
    }

    @Test
    @DisplayName("Service Layer - 구간 삭제 기능")
    void deleteSection() {
        //given
        _2호선.getSections().add(new Section(_2호선, 신도림역, 홍대입구역, 5));
        //when
        lineService.deleteSection(_2호선.getId(), 신도림역.getId());
        //then
        List<Station> stations = _2호선.getStations();
        List<Section> sections = _2호선.getSections();

        assertThat(stations).containsExactly(합정역, 홍대입구역);
        MatcherAssert.assertThat(sections, hasSize(1));
    }
}
