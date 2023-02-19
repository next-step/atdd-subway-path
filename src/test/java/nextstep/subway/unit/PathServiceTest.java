package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.exception.path.InvalidPathException;
import nextstep.subway.domain.exception.path.SourceAndTargetCannotBeSameException;
import nextstep.subway.domain.exception.path.StationNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.AssertionUtils.목록은_다음을_순서대로_포함한다;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 최단 경로 조회 관련")
@Transactional
@SpringBootTest
public class PathServiceTest {

    @Autowired
    private PathService pathService;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 신도림역;
    private Station 구로역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 일호선;
    private List<Line> 노선목록;

    /**
     * 교대역 --- *2호선(10)* --- 강남역
     * |                        |
     * *3호선(2)*             *신분당선(10)*
     * |                        |
     * 남부터미널역 --- *3호선(3)* -양재
     * <p>
     * 구로역 *1호선(10)* 신도림역
     */
    @BeforeEach
    public void setUp() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));
        신도림역 = stationRepository.save(new Station("신도림역"));
        구로역 = stationRepository.save(new Station("구로역"));


        이호선 = lineRepository.save(new Line("이호선", "green"));
        신분당선 = lineRepository.save(new Line("신분당선", "red"));
        삼호선 = lineRepository.save(new Line("삼호선", "orange"));
        일호선 = lineRepository.save(new Line("일호선", "blue"));

        lineService.addSection(이호선.getId(), new SectionRequest(교대역.getId(), 강남역.getId(), 10));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));
        lineService.addSection(삼호선.getId(), new SectionRequest(교대역.getId(), 남부터미널역.getId(), 2));
        lineService.addSection(일호선.getId(), new SectionRequest(신도림역.getId(), 구로역.getId(), 10));

        lineService.addSection(삼호선.getId(), new SectionRequest(남부터미널역.getId(), 양재역.getId(), 3));
    }

    @DisplayName("최단 경로를 조회한다.")
    @Test
    void findPath() {
        final PathResponse path = pathService.getShortestPath(new PathRequest(교대역.getId(), 양재역.getId()));

        final List<String> 경로의_역_이름_목록 = path.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> 목록은_다음을_순서대로_포함한다(경로의_역_이름_목록, 교대역.getName(), 남부터미널역.getName(), 양재역.getName())
        );
    }

    @DisplayName("노선에 등록되지 않은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenStationsNotRegistered() {
        final Station 부산역 = stationRepository.save(new Station("부산역"));

        assertThatThrownBy(() -> pathService.getShortestPath(new PathRequest(교대역.getId(), 부산역.getId())))
                .isInstanceOf(StationNotRegisteredException.class);
    }

    @DisplayName("출발역이 이어지지 않은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenSourceStationsNotLinked() {
        assertThatThrownBy(() -> pathService.getShortestPath(new PathRequest(신도림역.getId(), 강남역.getId())))
                .isInstanceOf(InvalidPathException.class);
    }

    @DisplayName("도착역이 이어지지 않은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenTargetStationsNotLinked() {
        assertThatThrownBy(() -> pathService.getShortestPath(new PathRequest(강남역.getId(), 신도림역.getId())))
                .isInstanceOf(InvalidPathException.class);
    }

    @DisplayName("출발역과 도착역이 같은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenSourceStationAndTargetStationAreSame() {
        assertThatThrownBy(() -> pathService.getShortestPath(new PathRequest(강남역.getId(), 강남역.getId())))
                .isInstanceOf(SourceAndTargetCannotBeSameException.class);
    }
}
