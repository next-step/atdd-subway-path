package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class PathFinderTest {

    private PathFinder pathFinder;

    @Autowired
    private LineService lineService;
    @Autowired
    private StationRepository stationRepository;

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;

    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = createStationStep("교대역");
        강남역 = createStationStep("강남역");
        양재역 = createStationStep("양재역");
        남부터미널역 = createStationStep("남부터미널역");

        이호선 = createLineStep("2호선", "green", 교대역, 강남역, 10);
        신분당선 = createLineStep("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = createLineStep("3호선", "orange", 교대역, 남부터미널역, 2);

        createSection(삼호선, 남부터미널역, 양재역, 3);
    }

    @Test
    void findShortestPath() {
        var response = pathFinder.solve(교대역, 양재역);

        assertThat(response).isEqualTo(new PathResponse(
                List.of(
                        new StationResponse(교대역, "교대역"),
                        new StationResponse(남부터미널역, "남부터미널역"),
                        new StationResponse(양재역, "양재역")
                ),
                5
        ));
    }

    private Long createStationStep(String name) {
        return stationRepository.save(new Station(name)).getId();
    }

    private Long createLineStep(String name, String color, Long upStation, Long downStation, Integer distance) {
        return lineService.saveLine(new LineRequest(name, color, upStation, downStation, distance)).getId();
    }

    private void createSection(Long line, Long upStation, Long downStation, Integer distance) {
        lineService.addSection(line, new SectionRequest(upStation, downStation, distance));
    }
}