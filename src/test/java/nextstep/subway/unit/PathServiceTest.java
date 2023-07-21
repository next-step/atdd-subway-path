package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.domain.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PathServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PathService pathService;

    Station 강남역;
    Station 양재역;
    Station 교대역;
    Station 남부터미널역;
    Station 독바위역;
    Station 불광역;
    Line 삼호선;
    Line 이호선;
    Line 신분당선;
    Line 육호선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        교대역 = stationRepository.save(new Station("교대역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));
        독바위역 = stationRepository.save(new Station("독바위역"));
        불광역 = stationRepository.save(new Station("불광역"));

        삼호선 = lineRepository.save(new Line("삼호선", "orange"));
        이호선 = lineRepository.save(new Line("이호선", "green"));
        신분당선 = lineRepository.save(new Line("신분당선", "red"));
        육호선 = lineRepository.save(new Line("육호선", "brown"));

        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        육호선.addSection(new Section(육호선, 독바위역, 불광역, 5));
    }

    @DisplayName("지하철 경로를 조회한다")
    @Test
    void getPaths() {
        // when
        PathResponse response = pathService.findPath(new PathRequest(교대역.getId(), 양재역.getId()));

        // then
        assertThat(response.getStations().stream().map(StationResponse::getName))
                .containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(response.getDistance()).isEqualTo(5);
    }

    @DisplayName("지하철 경로 조회 시 출발역과 도착역이 같으면 에러가 발생한다")
    @Test
    void getPaths_startAndEndStationEquals_Exception() {
        // when, then
        assertThatThrownBy(() -> pathService.findPath(new PathRequest(교대역.getId(), 교대역.getId())))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message()
                .isEqualTo("출발역과 도착역이 같습니다.");
    }

    @DisplayName("지하철 경로 조회 시 출발역과 도착역이 연결되어 있지 않으면 에러가 발생한다")
    @Test
    void getPaths_startAndEndStationNotConnection_Exception() {
        // when, then
        assertThatThrownBy(() -> pathService.findPath(new PathRequest(교대역.getId(), 독바위역.getId())))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message()
                .isEqualTo("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    @DisplayName("지하철 경로 조회 시 출발역 존재하지 않으면 에러가 발생한다")
    @Test
    void getPaths_startStationNotExist_Exception() {
        // given
        Long 존재하지_않는역 = 999L;

        // when, then
        assertThatThrownBy(() -> pathService.findPath(new PathRequest(존재하지_않는역, 교대역.getId())))
                .isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("지하철 경로 조회 시 도착역이 존재하지 않으면 에러가 발생한다")
    @Test
    void getPaths_EndStationNotExist_Exception() {
        // given
        Long 존재하지_않는역 = 999L;

        // when, then
        assertThatThrownBy(() -> pathService.findPath(new PathRequest(교대역.getId(), 존재하지_않는역)))
                .isInstanceOf(StationNotFoundException.class);
    }
}