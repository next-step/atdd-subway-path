package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@DisplayName("지하철 경로 조회 서비스 단위 테스트")
@Transactional
@SpringBootTest
class PathServiceTest {
    private Station gyodaeStation;
    private Station gangnamStation;
    private Station yangjaeStation;
    private Station nambuTerminalStation;
    private Line lineTwo;
    private Line lineShinbundang;
    private Line lineThree;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    /**
     * 교대역   ---   *2호선(10km)*  ---   강남역
     * |                                  |
     * *3호선(2km)*                   *신분당선(10km)*
     * |                                  |
     * 남부터미널역 --- *3호선(3km)*   ---   양재
     */
    @BeforeEach
    void setUp() {
        gyodaeStation = new Station("교대역");
        gangnamStation = new Station("강남역");
        yangjaeStation = new Station("양재역");
        nambuTerminalStation = new Station("남부터미널역");

        lineTwo = new Line("2호선", "green");
        lineTwo.addSection(gyodaeStation, gangnamStation, 10);
        lineShinbundang = new Line("신분당선", "red");
        lineShinbundang.addSection(gangnamStation, yangjaeStation, 10);
        lineThree = new Line("3호선", "orange");
        lineThree.addSection(gyodaeStation, nambuTerminalStation, 2);
        lineThree.addSection(nambuTerminalStation, yangjaeStation, 3);
    }

    /**
     * given : 지하철 노선을 저장하고
     * when : 두 지하철 역을 기준으로 지하철 노선의 최단 경로를 조회 하면
     * then : 최단 경로의 지하철 역들과 거리를 알 수 있다.
     */
    @Test
    void getShortestPath() {
        //given
        lineRepository.save(lineTwo);
        lineRepository.save(lineThree);
        lineRepository.save(lineShinbundang);

        //when
        PathResponse shortestPath = pathService.getShortestPath(1L, 3L);

        //then
        List<String> StationNames = getStationNames(shortestPath);
        Assertions.assertThat(StationNames).containsExactly("강남역", "교대역", "남부터미널역");
        Assertions.assertThat(shortestPath.getDistance()).isEqualTo(12);
    }

    private List<String> getStationNames(PathResponse shortestPath) {
        return shortestPath.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
