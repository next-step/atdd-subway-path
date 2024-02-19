package nextstep.subway.unit.service;

import nextstep.subway.common.Constant;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.response.ShowLineResponse;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.path.service.PathService;
import nextstep.subway.section.service.dto.ShowLineSectionDto;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.service.StationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.acceptance.path.PathAcceptanceStep.지하철_최단_경로_조회;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PathService pathService;

    private Station 교대역;
    private Long 교대역_ID;
    private Station 강남역;
    private Long 강남역_ID;
    private Station 양재역;
    private Long 양재역_ID;
    private Station 남부터미널역;
    private Long 남부터미널역_ID;

    private Line 이호선;
    private Long 이호선_ID;
    private Line 삼호선;
    private Long 삼호선_ID;
    private Line 신분당선;
    private Long 신분당선_ID;
    
    @BeforeEach
    protected void setUp() {
        이호선 = lineRepository.save(Line.of(Constant.이호선, Constant.초록색));
        이호선_ID = 이호선.getLineId();
//        삼호선 = lineRepository.save(Line.of(Constant.삼호선, Constant.주황색));
//        삼호선_ID = 삼호선.getLineId();
        신분당선 = lineRepository.save(Line.of(Constant.신분당선, Constant.빨간색));
        신분당선_ID = 신분당선.getLineId();

        교대역 = stationRepository.save(Station.from(Constant.교대역));
        교대역_ID = 교대역.getStationId();
        강남역 = stationRepository.save(Station.from(Constant.강남역));
        강남역_ID = 강남역.getStationId();
        양재역 = stationRepository.save(Station.from(Constant.양재역));
        양재역_ID = 양재역.getStationId();
//        남부터미널역 = stationRepository.save(Station.from(Constant.남부터미널역));
//        남부터미널역_ID = 남부터미널역.getStationId();
    }

    @DisplayName("같은 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    void 같은_노선의_출발역과_도착역의_최단_경로_조회() {
        // when
        FindPathResponse 교대역_양재역_경로_조회_응답 = pathService.findShortestPath(교대역_ID, 양재역_ID);

        // then
        assertThat(교대역_양재역_경로_조회_응답.getDistance()).isEqualTo(Constant.역_간격_15 + Constant.역_간격_10);
        assertThat(교대역_양재역_경로_조회_응답.getStations()).hasSize(2);
    }

    @DisplayName("여러 출발역과 도착역의 최단 경로를 조회한다.")
    @Test
    @Disabled
    void 여러_노선의_출발역과_도착역의_최단_경로_조회() {
    }

}
