package nextstep.subway.path.application;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private PathService pathService;

    private Station 방배역;
    private Station 서초역;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 양재역;
    private Station 양재시민의숲역;
    private Station 청계산입구역;
    private Station 판교역;
    private Station 광교역;

    private Line 이호선;
    private Line 신분당선;

    private final static int DEFAULT_SECTION_DISTANCE = 10;

    @BeforeEach
    public void setUp() {
        방배역 = stationRepository.save(new Station("방배역"));
        서초역 = stationRepository.save(new Station("서초역"));
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));

        양재역 = stationRepository.save(new Station("양재역"));
        양재시민의숲역 = stationRepository.save(new Station("양재시민의숲역"));
        청계산입구역 = stationRepository.save(new Station("청계산입구역"));
        판교역 = stationRepository.save(new Station("판교역"));
        광교역 = stationRepository.save(new Station("광교역"));

        //2호선 생성
        이호선 = lineRepository.save(new Line("이호선", "green", 방배역, 서초역, 10));

        //신분당선 생성
        신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역, 양재역, 10));

        //section 등록
        lineService.addSection(이호선, 서초역, 교대역, DEFAULT_SECTION_DISTANCE);
        lineService.addSection(이호선, 교대역, 강남역, DEFAULT_SECTION_DISTANCE);
        lineService.addSection(이호선, 강남역, 역삼역, DEFAULT_SECTION_DISTANCE);

        lineService.addSection(신분당선, 양재역, 양재시민의숲역, DEFAULT_SECTION_DISTANCE);
        lineService.addSection(신분당선, 양재시민의숲역, 청계산입구역, DEFAULT_SECTION_DISTANCE);
        lineService.addSection(신분당선, 청계산입구역, 판교역, DEFAULT_SECTION_DISTANCE);
    }

    @DisplayName("역에서 역 경로를 조회하는데 등록되지 않은 시작 역이면 오류")
    @Test
    public void finePathWithNotRegisteredSource() {
        assertThatThrownBy(() -> pathService.findPath(100L, 판교역.getId()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("등록되지 않은 역 입니다.");
    }

    @DisplayName("역에서 역 경로를 조회하는데 등록되지 않은 도착 역이면 오류")
    @Test
    public void finePathWithNotRegisteredTarget() {
        assertThatThrownBy(() -> pathService.findPath(방배역.getId(), 101L))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("등록되지 않은 역 입니다.");
    }

    @DisplayName("시작역과 도착역을 동일하게 넣으면 오류")
    @Test
    public void finePathWithSameSourceIdAndTargetId() {
        assertThatThrownBy(() -> pathService.findPath(판교역.getId(), 판교역.getId()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("시작역과 도착역은 동일할 수 없습니다.");
    }

    @DisplayName("역이 연결되어있지 않으면 오류")
    @Test
    public void findPathWithNotConnectedStations() {
        assertThatThrownBy(() -> pathService.findPath(역삼역.getId(), 광교역.getId()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("역이 연결되어있지 않습니다.");
    }

    @DisplayName("역 조회")
    @Test
    public void findPathWith() {
        //when
        PathResponse response = pathService.findPath(교대역.getId(), 판교역.getId());

        //then
        assertThat(response.getDistance()).isEqualTo(5*DEFAULT_SECTION_DISTANCE);
        assertThat(response.getStations().size()).isEqualTo(6);
        assertThat(response.getStations().get(0)).isEqualTo(교대역);
        assertThat(response.getStations().get(response.getStations().size()-1)).isEqualTo(판교역);
    }
}
