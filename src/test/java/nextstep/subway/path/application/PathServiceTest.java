package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.DoseNotConnectedException;
import nextstep.subway.path.exception.EqualsStationsException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.DoseNotExistedStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("PathService 단위 테스트")
@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private PathFinder pathFinder;

    @Autowired
    private PathService pathService;

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    @BeforeEach
    void setup() {

        System.out.println("==========setup==========");

        강남역 = stationService.saveStation(new StationRequest("강남역"));
        양재역 = stationService.saveStation(new StationRequest("양재역"));
        교대역 = stationService.saveStation(new StationRequest("교대역"));
        남부터미널역 = stationService.saveStation(new StationRequest("남부터미널역"));

        신분당선 = lineService.saveLine(
            new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10)
        );
        이호선 = lineService.saveLine(
            new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10)
        );
        삼호선 = lineService.saveLine(
            new LineRequest("삼호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5)
        );

        lineService.addSection(삼호선.getId(), new SectionRequest(교대역.getId(), 남부터미널역.getId(), 3));
    }


    /**
     * 교대역    --- *2호선* ---   강남역
     * |                           |
     * *3호선*                   *신분당선*
     * |                           |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @DisplayName("지하철 경로 조회: 정상")
    @Test
    public void getPath() {
        // given
        long source = 강남역.getId();
        long target = 남부터미널역.getId();

        // when
        PathResponse path = pathService.getPath(source, target);

        // then
        assertThat(
            path.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList())
        ).isEqualTo(Arrays.asList(강남역.getId(), 양재역.getId(), 남부터미널역.getId()));
    }

    @DisplayName("지하철 경로 조회 예외: 출발역과 도착역이 같은 경우")
    @Test
    public void getPathExceptionThatEquals() {
        // given
        long source = 강남역.getId();
        long target = 강남역.getId();

        // when - then
        assertThatExceptionOfType(EqualsStationsException.class)
            .isThrownBy(() -> pathService.getPath(source, target));
    }

    @DisplayName("지하철 경로 조회 예외: 출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    public void getPathExceptionThatDoesNotConnected() {
        // given
        StationResponse 수서역 = stationService.saveStation(new StationRequest("수서역"));
        StationResponse 가천대역 = stationService.saveStation(new StationRequest("가천대역"));

        LineResponse 분당선 = lineService.saveLine(
            new LineRequest("분당선", "yellow", 수서역.getId(), 가천대역.getId(), 10)
        );

        // when - then
        for (StationResponse target: Arrays.asList(수서역, 가천대역)) {
            assertThatExceptionOfType(DoseNotConnectedException.class)
                .isThrownBy(() -> pathService.getPath(남부터미널역.getId(), target.getId()));
        }
    }

    @DisplayName("지하철 경로 조회 예외: 존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    public void getPathExceptionThatDoesNotExisted() {
        // given
        StationResponse 수서역 = stationService.saveStation(new StationRequest("수서역"));
        StationResponse 가천대역 = stationService.saveStation(new StationRequest("가천대역"));


        // when - then
        assertThatExceptionOfType(DoseNotExistedStationException.class)
            .isThrownBy(() -> pathService.getPath(수서역.getId(), 가천대역.getId()));

        for (StationResponse target: Arrays.asList(수서역, 가천대역)) {
            assertThatExceptionOfType(DoseNotExistedStationException.class)
                .isThrownBy(() -> pathService.getPath(남부터미널역.getId(), target.getId()));
        }
    }

}
