package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.PathNotFoundException;
import nextstep.subway.exception.PathSameStationException;
import nextstep.subway.exception.StationNotExistException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class PathServiceTest {
    @Autowired
    private PathService pathService;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    LineResponse 이호선;
    LineResponse 신분당선;
    LineResponse 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));
        이호선 = lineService.saveLine(new LineRequest("2호선", "bg-green-900", 교대역.getId(), 강남역.getId(), 10));
        신분당선 = lineService.saveLine(new LineRequest("신분당선", "bg-red-900", 강남역.getId(), 양재역.getId(), 10));
        삼호선 = lineService.saveLine(new LineRequest("3호선", "bg-orange-900", 남부터미널역.getId(), 양재역.getId(), 2));
        lineService.addSection(삼호선.getId(), new SectionRequest(교대역.getId(), 남부터미널역.getId(), 12));
    }

    @Test
    @Transactional
    public void findPath() {
        // given


        // when
        PathResponse pathResponse = pathService.getPath(교대역.getId(), 양재역.getId());

        // then
        assertThat(pathResponse.getStations())
                .map(StationResponse::getName)
                .containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName());
    }

    @Test
    @Transactional
    @DisplayName("출발역과 도착역이 같은 경우 예외 발생")
    public void sameSourceAndDestination() {
        // given

        // when
        // then
        assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 교대역.getId()))
                .isInstanceOf(PathSameStationException.class);
    }

    @Test
    @Transactional
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    public void notExistStation() {
        // given
        // when
        // then
        assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 100L))
                .isInstanceOf(StationNotExistException.class);
    }

    @Test
    @Transactional
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    public void notLinkSourceAndDestination() {
        // given
        Station 월계역 = stationRepository.save(new Station("월계역"));
        Station 광운대역 = stationRepository.save(new Station("광운대역"));
        LineResponse 일호선 = lineService.saveLine(new LineRequest("1호선", "bg-blue-900", 월계역.getId(), 광운대역.getId(), 10));
        // when
        // then
        assertThatThrownBy(() -> pathService.getPath(교대역.getId(), 월계역.getId()))
                .isInstanceOf(PathNotFoundException.class);
    }
}
