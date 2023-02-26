package nextstep.subway.unit;

import nextstep.subway.applicaion.JGraphPathFinder;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

@DisplayName("지하철 경로 조회 서비스 Mock 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
    private Station gyodaeStation;
    private Station gangnamStation;
    private Station yangjaeStation;
    private Station nambuTerminalStation;
    private Line lineTwo;
    private Line lineShinbundang;
    private Line lineThree;
    private List<Line> allLines;

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @Mock
    private JGraphPathFinder pathFinder;

    @InjectMocks
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
        allLines = List.of(lineTwo, lineThree, lineShinbundang);
    }

    /**
     * given : 테스트에 필요한 협력 객체를 stubbing 처리하고
     * when : 지하철 노선의 최단 경로를 조회 하면
     * then : 최단 경로의 지하철 역과 거리를 알 수 있다.
     */
    @Test
    void getShortestPath() {
        //given
        BDDMockito.given(lineService.findAll()).willReturn(allLines);
        BDDMockito.given(stationService.findById(1L)).willReturn(gangnamStation);
        BDDMockito.given(stationService.findById(3L)).willReturn(nambuTerminalStation);
        BDDMockito.given(pathFinder.find(allLines, gangnamStation, nambuTerminalStation)).willReturn(new PathResponse(List.of(gangnamStation, gyodaeStation, nambuTerminalStation), 12));

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
