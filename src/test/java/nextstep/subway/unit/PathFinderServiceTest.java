package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathFinderService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("경로 조회 테스트")
@SpringBootTest
public class PathFinderServiceTest {
    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @Autowired
    PathFinderService pathFinderService;

    @Autowired
    LineService lineService;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    Long 교대역;
    Long 강남역;
    Long 양재역;
    Long 남부터미널역;
    Long 이호선;
    Long 삼호선;
    Long 신분당선;

    @BeforeEach
    void init() {
        교대역 = stationRepository.save(new Station("교대역")).getId();
        강남역 = stationRepository.save(new Station("강남역")).getId();
        양재역 = stationRepository.save(new Station("양재역")).getId();
        남부터미널역 = stationRepository.save(new Station("남부터미널역")).getId();
        이호선 = lineRepository.save(new Line("이호선", "green")).getId();
        삼호선 = lineRepository.save(new Line("삼호선", "orange")).getId();
        신분당선 = lineRepository.save(new Line("신분당선", "red")).getId();

        lineService.addSection(이호선, createSectionRequest(교대역, 강남역, 10));
        lineService.addSection(삼호선, createSectionRequest(교대역, 남부터미널역, 2));
        lineService.addSection(삼호선, createSectionRequest(남부터미널역, 양재역, 3));
        lineService.addSection(신분당선, createSectionRequest(강남역, 양재역, 10));
    }

    @DisplayName("최단경로를 조회할 수 있다.")
    @Test
    void 최단경로_조회() {
        PathResponse shortestPath = pathFinderService.getShortestPath(교대역, 양재역);
        List<Long> ids = shortestPath.getStationList().stream().map(StationResponse::getId).collect(Collectors.toList());

        assertThat(ids).containsExactly(교대역, 강남역, 양재역);
    }

    @DisplayName("출발역과 도착역이 같은 경우 에러가 발생한다.")
    @Test
    void 출발역_도착역_같음_에러() {
        assertThatThrownBy(() -> pathFinderService.getShortestPath(교대역, 교대역))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.DUPLICATED_PATH_FIND.toString());
    }

    @DisplayName("출발역과 도착역이 연결되지 않으면 에러가 발생한다.")
    @Test
    void 출발역_도착역_연결되지않음_에러() {
        Long 신사역 = stationRepository.save(new Station(9L, "신사역")).getId();

        assertThatThrownBy(() -> pathFinderService.getShortestPath(교대역, 신사역))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.NOT_CONNECT_PATH.toString());
    }

    @DisplayName("존재하지 않은 출발역으로 경로를 조회하면 에러가 발생한다.")
    void 존재하지않은_출발역_에러() {
        Long 출발역 = 999L;
        assertThatThrownBy(() -> pathFinderService.getShortestPath(출발역, 교대역))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.NOT_FOUND_SOURCE.toString());

    }
    @DisplayName("존재하지 않은 도착역으로 경로를 조회하면 에러가 발생한다.")
    void 존재하지않은_도착역_에러() {
        Long 도착역 = 999L;
        assertThatThrownBy(() -> pathFinderService.getShortestPath(교대역, 도착역))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ErrorMessage.NOT_FOUND_TARGET.toString());
    }

    SectionRequest createSectionRequest(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }



}
