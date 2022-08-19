package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PathServiceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 신분당선;
    private Long 이호선;
    private Long 삼호선;

    @Autowired
    private LineService lineService;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private PathService pathService;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     */
    @BeforeEach
    void setUp() {
        교대역 = stationRepository.save(new Station("교대역")).getId();
        강남역 = stationRepository.save(new Station("강남역")).getId();
        양재역 = stationRepository.save(new Station("양재역")).getId();
        남부터미널역 = stationRepository.save(new Station("남부터미널역")).getId();

        신분당선 = lineRepository.save(new Line("신분당선", "red")).getId();
        이호선 = lineRepository.save(new Line("이호선", "green")).getId();
        삼호선 = lineRepository.save(new Line("삼호선", "orange")).getId();

        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 10));
        lineService.addSection(이호선, new SectionRequest(교대역, 강남역, 10));
        lineService.addSection(삼호선, new SectionRequest(교대역, 남부터미널역, 2));
        lineService.addSection(삼호선, new SectionRequest(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역에 대한 경로를 조회하면
     * Then 경로에 있는 역 목록과 거리를 응답한다.
     */
    @DisplayName("서로 다른 연결된 역 두개의 경로를 조회한다.")
    @Test
    void pathSearchSuccess() {
        // when
        PathResponse response = pathService.findPath(교대역, 양재역);

        // then
        List<Long> stationIds = response.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(stationIds).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(response.getDistance()).isEqualTo(5)
        );
    }

    /**
     * When 출발역과 도착역을 동일한 역으로 경로를 조회하면
     * Then 경로 조회에 실패한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 경로 조회를 실패한다.")
    @Test
    void sameStartAndEndStationFindLineFail() {
        // when & then
        assertThatThrownBy(() -> pathService.findPath(교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given 서로 연결되어있지 않는 지하철 노선을 추가하고
     * When 연결되지 않은 역끼리 경로를 조회하면 Then 경로 조회에 실패한다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 경로 조회는 실패한다.")
    @Test
    void startAndEndStationIsNotLinkFindLineFail() {
        // given
        Long 신논현역 = stationRepository.save(new Station("신논현역")).getId();
        Long 언주역 = stationRepository.save(new Station("언주역")).getId();
        Long 구호선 = lineRepository.save(new Line("9호선", "brown")).getId();
        lineService.addSection(구호선, new SectionRequest(신논현역, 언주역, 10));

        // when & then
        assertThatThrownBy(() -> pathService.findPath(신논현역, 교대역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * When 존재하지 않는 역의 경로를 조회하면
     * Then 경로 조회에 실패한다.
     */
    @DisplayName("출발역 또는 도착역이 존재하지 않는 경우 경로 조회에 실패한다.")
    @Test
    void startOrEndStationNotExistFindLineFail() {
        // when & then
        assertThatThrownBy(() -> pathService.findPath(교대역, 99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}