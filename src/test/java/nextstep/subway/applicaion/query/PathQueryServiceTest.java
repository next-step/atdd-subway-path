package nextstep.subway.applicaion.query;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.station.StationNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
@Transactional
@DisplayName("지하철 경로 탐색")
class PathQueryServiceTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private PathQueryService pathQueryService;

    private Station 교대역;
    private Station 남부터미널역;
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = Station.of("교대역");
        남부터미널역 = Station.of("남부터미널역");
        강남역 = Station.of("강남역");
        양재역 = Station.of("양재역");
        판교역 = Station.of("판교역");
        stationRepository.save(교대역);
        stationRepository.save(남부터미널역);
        stationRepository.save(강남역);
        stationRepository.save(양재역);
        stationRepository.save(판교역);

        신분당선 = Line.of("신분당선", "red", 강남역, 양재역, 100);
        신분당선.addSection(양재역, 판교역, 100);

        이호선 = Line.of("이호선", "green", 교대역, 강남역, 50);

        삼호선 = Line.of("삼호선", "orange", 교대역, 남부터미널역, 20);
        삼호선.addSection(남부터미널역, 양재역, 20);

        lineRepository.save(신분당선);
        lineRepository.save(이호선);
        lineRepository.save(삼호선);
    }

    @DisplayName("가장 빠른 경로 찾기")
    @Test
    void findPath() {
        // when
        PathResponse response = pathQueryService.findPath(교대역.getId(), 양재역.getId());

        // then
        List<String> names = names(response);
        Assertions.assertThat(names).containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName());
    }

    @DisplayName("없는 역을 요청할 경우 예외 처리")
    @Test
    void findPath_notFoundStation() {
        // given
        long 없는역Id = 1000;

        // then
        Assertions.assertThatThrownBy(() -> pathQueryService.findPath(교대역.getId(), 없는역Id))
                .isInstanceOf(StationNotFoundException.class);
    }

    private List<String> names(PathResponse response) {
        return response.getStations()
                .stream()
                .map(it -> it.getName())
                .collect(Collectors.toList());
    }

}