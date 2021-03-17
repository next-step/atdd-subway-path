package nextstep.subway.paths.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.paths.dto.PathResponse;
import nextstep.subway.paths.dto.PathStationResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PathServiceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 사호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 사당역;
    private StationResponse 남태령;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private PathFinder pathFinder;

    @BeforeEach
    public void setUp() {
        강남역 = stationService.saveStation(new StationRequest("강남역"));
        양재역 = stationService.saveStation(new StationRequest("양재역"));
        교대역 = stationService.saveStation(new StationRequest("교대역"));
        남부터미널역 = stationService.saveStation(new StationRequest("남부터미널역"));

        신분당선 = lineService.saveLine(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10));
        이호선 = lineService.saveLine(new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10));
        삼호선 = lineService.saveLine(new LineRequest("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5));

        final Line 이호선_노선 = lineService.findLineById(삼호선.getId());
        final Station 교대지하철역 = stationService.findStationById(교대역.getId());
        final Station 남부터미널지하철역 = stationService.findStationById(남부터미널역.getId());
        lineService.addSection(이호선_노선, 교대지하철역, 남부터미널지하철역, 3);
    }

    @DisplayName("최단 경로가 찾은 경우")
    @Test
    void searchShortestPath() {
        // given
        final Station 교대지하철역 = stationService.findStationById(교대역.getId());
        final Station 남부터미널지하철역 = stationService.findStationById(남부터미널역.getId());
        List<Line> lines = lineService.findAllLines();
        pathFinder.initialize(lines);

        // when
        PathResponse response = pathFinder.searchShortestPath(교대지하철역, 남부터미널지하철역);

        // then
        List<PathStationResponse> expectedPathStations = Stream.of(교대지하철역, 남부터미널지하철역)
                .map(station -> PathStationResponse.of(station))
                .collect(Collectors.toList());
        assertThat(response.getStations()).isEqualTo(expectedPathStations);
        assertThat(response.getDistance()).isEqualTo(3);
    }

    @DisplayName("전달한 역의 아이디가 잘못 된 경우")
    @Test
    void searchShortestPathWithInvalidStationId() {

    }
}
