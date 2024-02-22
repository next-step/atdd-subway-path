package nextstep.subway.unit;

import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.PathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathServiceTest {
    @Autowired
    private PathService pathService;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        stationRepository.saveAll(List.of(교대역, 강남역, 양재역, 남부터미널역));

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
        lineRepository.saveAll(List.of(이호선, 신분당선, 삼호선));
    }

    @DisplayName("출발역과 도착역을 통해 최단 경로를 조회한다.")
    @Test
    void getPath() {
        // when
        PathResponse 경로_조회_응답 = pathService.getPaths(교대역.getId(), 양재역.getId());

        // then
        List<Long> 최단구간_역_목록 = 경로_조회_응답.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(최단구간_역_목록).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(경로_조회_응답.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 동일한 경우 예외가 발생한다.")
    @Test
    void 동일한_도착역과_출발역() {
        assertThatThrownBy(() -> pathService.getPaths(교대역.getId(), 교대역.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("출발역과 도착역은 동일할 수 없다.");
    }

    @DisplayName("출발역과 도착역이 존재하지 않는 경우 예외가 발생한다.")
    @Test
    void 존재하지_않는_역() {
        Long 존재하지_않는_역 = -9999L;

        assertThatThrownBy(() -> pathService.getPaths(교대역.getId(), 존재하지_않는_역))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
