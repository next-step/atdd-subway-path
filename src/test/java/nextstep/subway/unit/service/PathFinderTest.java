package nextstep.subway.unit.service;

import nextstep.subway.common.Constant;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.SameFindPathStationsException;
import nextstep.subway.exception.UnconnectedFindPathStationsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.service.LineService;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.path.service.PathFinder;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathFinderTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;
    @Autowired
    private PathFinder pathFinder;

    private Station 교대역;
    private Long 교대역_ID;
    private Station 논현역;
    private Long 논현역_ID;
    private Station 신논현역;
    private Long 신논현역_ID;
    private Station 강남역;
    private Long 강남역_ID;
    private Station 양재역;
    private Long 양재역_ID;
    private Station 남부터미널역;
    private Long 남부터미널역_ID;
    private Station 압구정로데오역;
    private Long 압구정로데오역_ID;
    private Station 강남구청역;
    private Long 강남구청역_ID;

    private Line 이호선;
    private Long 이호선_ID;
    private Line 삼호선;
    private Long 삼호선_ID;
    private Line 신분당선;
    private Long 신분당선_ID;
    private Line 수인분당선;
    private Long 수인분당선_ID;

    private List<Line> 노선들;

    private AddSectionRequest 교대역_강남역_구간;
    private AddSectionRequest 교대역_남부터미널역_구간;
    private AddSectionRequest 남부터미널역_양재역_구간;
    private AddSectionRequest 논현역_신논현역_구간;
    private AddSectionRequest 신논현역_강남역_구간;
    private AddSectionRequest 강남역_양재역_구간;
    private AddSectionRequest 압구정로데오역_강남구청역_구간;

    @BeforeEach
    protected void setUp() {
        교대역 = stationRepository.save(Station.from(Constant.교대역));
        교대역_ID = 교대역.getStationId();
        논현역 = stationRepository.save(Station.from(Constant.논현역));
        논현역_ID = 논현역.getStationId();
        신논현역 = stationRepository.save(Station.from(Constant.신논현역));
        신논현역_ID = 신논현역.getStationId();
        강남역 = stationRepository.save(Station.from(Constant.강남역));
        강남역_ID = 강남역.getStationId();
        양재역 = stationRepository.save(Station.from(Constant.양재역));
        양재역_ID = 양재역.getStationId();
        남부터미널역 = stationRepository.save(Station.from(Constant.남부터미널역));
        남부터미널역_ID = 남부터미널역.getStationId();
        압구정로데오역 = stationRepository.save(Station.from(Constant.압구정로데오역));
        압구정로데오역_ID = 압구정로데오역.getStationId();
        강남구청역 = stationRepository.save(Station.from(Constant.강남구청역));
        강남구청역_ID = 강남구청역.getStationId();

        이호선 = lineRepository.save(Line.of(Constant.이호선, Constant.초록색));
        이호선_ID = 이호선.getLineId();
        삼호선 = lineRepository.save(Line.of(Constant.삼호선, Constant.주황색));
        삼호선_ID = 삼호선.getLineId();
        신분당선 = lineRepository.save(Line.of(Constant.신분당선, Constant.빨간색));
        신분당선_ID = 신분당선.getLineId();
        수인분당선 = lineRepository.save(Line.of(Constant.수인분당선, Constant.노란색));
        수인분당선_ID = 수인분당선.getLineId();

        교대역_강남역_구간 = AddSectionRequest.of(교대역_ID, 강남역_ID, Constant.역_간격_10);
        교대역_남부터미널역_구간 = AddSectionRequest.of(교대역_ID, 남부터미널역_ID, Constant.역_간격_10);
        남부터미널역_양재역_구간 = AddSectionRequest.of(남부터미널역_ID, 양재역_ID, Constant.역_간격_15);
        논현역_신논현역_구간 = AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_15);
        신논현역_강남역_구간 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10);
        강남역_양재역_구간 = AddSectionRequest.of(강남역_ID, 양재역_ID, Constant.역_간격_10);
        압구정로데오역_강남구청역_구간 = AddSectionRequest.of(압구정로데오역_ID, 강남구청역_ID, Constant.역_간격_10);

        lineService.addSection(이호선_ID, 교대역_강남역_구간);
        lineService.addSection(삼호선_ID, 교대역_남부터미널역_구간);
        lineService.addSection(삼호선_ID, 남부터미널역_양재역_구간);
        lineService.addSection(신분당선_ID, 논현역_신논현역_구간);
        lineService.addSection(신분당선_ID, 신논현역_강남역_구간);
        lineService.addSection(신분당선_ID, 강남역_양재역_구간);
        lineService.addSection(수인분당선_ID, 압구정로데오역_강남구청역_구간);

        노선들 = lineRepository.findAll();
    }

    @DisplayName("같은 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void 같은_노선의_출발역과_도착역의_최단_경로_조회() {
        // when
        FindPathResponse 교대역_양재역_경로_조회_응답 = pathFinder.findShortestPath(노선들, 논현역, 강남역);

        // then
        assertThat(교대역_양재역_경로_조회_응답.getDistance()).isEqualTo(논현역_신논현역_구간.getDistance() + 신논현역_강남역_구간.getDistance());
        assertThat(교대역_양재역_경로_조회_응답.getStations()).hasSize(3);
        assertThat(교대역_양재역_경로_조회_응답.getStations().stream()
                .map(stationDto -> stationDto.getName())
                .collect(Collectors.toList())
        ).containsExactly(Constant.논현역, Constant.신논현역, Constant.강남역);
    }

    @DisplayName("여러 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void 여러_노선의_출발역과_도착역의_최단_경로_조회() {
        // when
        FindPathResponse 교대역_양재역_경로_조회_응답 = pathFinder.findShortestPath(노선들, 교대역, 양재역);

        // then
        assertThat(교대역_양재역_경로_조회_응답.getDistance()).isEqualTo(교대역_강남역_구간.getDistance() + 강남역_양재역_구간.getDistance());
        assertThat(교대역_양재역_경로_조회_응답.getStations()).hasSize(3);
        assertThat(교대역_양재역_경로_조회_응답.getStations().stream()
                .map(stationDto -> stationDto.getName())
                .collect(Collectors.toList())
        ).containsExactly(Constant.교대역, Constant.강남역, Constant.양재역);
    }

    @DisplayName("출발역과 도착역이 동일할 경우 경로 조회시 예외발생가 발생한다.")
    @Test
    void 출발역과_도착역이_동일하게_경로_조회시_예외발생() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findShortestPath(노선들, 양재역, 양재역))
                .isInstanceOf(SameFindPathStationsException.class);
        ;
    }

    /**
     * When 존재하지 않은 출발역이나 도착역을 조회 할 경우
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회할 경우 예외발생")
    @Test
    void 존재하지_않은_출발역이나_도착역_경로_조회시_예외발생() {
        // given
        Station 왕십리역 = Station.from("왕십리역");

        // when & then
        assertThatThrownBy(() -> pathFinder.findShortestPath(노선들, 양재역, 왕십리역))
                .isInstanceOf(NotFoundStationException.class);
    }

    /**
     * When 출발역과 도착역이 연결이 되어 있지 않은 경우
     * Then 경로가 조회되지 않는다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 경로를 조회할 수 없다.")
    @Test
    void 연결되지_않은_출발역과_도착역_경로_조회시_예외발생() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findShortestPath(노선들, 양재역, 압구정로데오역))
                .isInstanceOf(UnconnectedFindPathStationsException.class);
    }

}
