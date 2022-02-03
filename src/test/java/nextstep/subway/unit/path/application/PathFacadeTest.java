package nextstep.subway.unit.path.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.repository.LineRepository;
import nextstep.subway.path.application.PathFacade;
import nextstep.subway.path.application.dto.PathRequest;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.repository.StationRepository;
import nextstep.subway.utils.DatabaseCleanup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PathFacadeTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private PathFacade pathFacade;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;
    @Autowired
    private StationRepository stationRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    @BeforeEach
    void setUp() {
        databaseCleanup.execute();

        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        Distance distance10 = new Distance(10);
        Distance distance2 = new Distance(2);
        String color = "black";
        Line 이호선 = lineRepository.save(new Line("이호선", color));
        Line 신분당선 = lineRepository.save(new Line("신분당선", color));
        Line 삼호선 = lineRepository.save(new Line("삼호선", color));

        addSection(이호선, 교대역, 강남역, distance10);
        addSection(신분당선, 강남역, 양재역, distance10);
        addSection(삼호선, 교대역, 남부터미널역, distance2);
        addSection(삼호선, 남부터미널역, 양재역, distance10);
    }

    private void addSection(Line line, Station upStation, Station downStation, Distance distance) {
        SectionRequest request = SectionRequest.builder()
            .upStationId(upStation.getId())
            .downStationId(downStation.getId())
            .distance(distance)
            .build();
        lineService.addSection(line.getId(), request);
    }

    @DisplayName("최단 거리 경로 찾기")
    @Test
    void findShortestPaths() {
        PathRequest pathRequest = PathRequest.builder()
            .source(교대역.getId())
            .target(양재역.getId())
            .build();

        PathResponse pathResponse = pathFacade.findShortestPaths(pathRequest);

        List<Long> stationIds = pathResponse.getStations().stream()
                                            .map(StationResponse::getId)
                                            .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(pathResponse.getDistance()).isEqualTo(12);
    }
}
