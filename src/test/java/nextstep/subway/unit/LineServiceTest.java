package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private Station 강남역;
    private Station 양재역;
    private Line 신분당선;

    @BeforeEach
    public void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 등록한다")
    void addSection() {
        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 제거한다")
    void deleteSection() {
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        lineService.deleteSection(신분당선.getId(), 양재역.getId());

        // then
        assertThat(신분당선.getSections().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록할 경우 지하철 역목록 조회")
    void getStations() {
        Station 고속터미널역 = stationRepository.save(new Station("고속터미널역"));
        Station 교대역 = stationRepository.save(new Station("교대역"));

        // when
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 고속터미널역.getId(), 10));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 교대역.getId(), 10));

        // then
        assertThat(신분당선.getStations().stream().mapToLong(Station::getId))
                .containsExactly(강남역.getId(), 교대역.getId(), 양재역.getId(), 고속터미널역.getId());
    }
}
