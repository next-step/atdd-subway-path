package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.common.exception.NoPathConnectedException;
import nextstep.subway.common.exception.NoRegisterStationException;
import nextstep.subway.common.exception.SameStationException;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.error.SubwayError.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("최소 경로 찾기의 대한 테스트")
@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 부평역;
    private Station 검암역;
    private Station 몽촌토성역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 일호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {

        교대역 = createStation("교대역");
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        남부터미널역 = createStation("남부터미널역");
        부평역 = createStation("부평역");
        검암역 = createStation("검암역");
        몽촌토성역 = createStation("몽촌토성역");

        final Section one = new Section(교대역, 강남역, 10);
        이호선 = createLineBySections("2호선", "green", List.of(one));
        final Section two = new Section(강남역, 양재역, 10);
        신분당선 = createLineBySections("신분당선", "red", List.of(two));
        final Section three = new Section(교대역, 남부터미널역, 2);
        final Section four = new Section(남부터미널역, 양재역, 3);
        삼호선 = createLineBySections("3호선", "orange", List.of(three, four));
        final Section five = new Section(부평역, 검암역, 5);
        일호선 = createLineBySections("1호선", "blue", List.of(five));
    }

    @DisplayName("최적의 경로를 찾는다.")
    @Test
    void findRouteTest() {

        final PathResponse pathResponse = pathService.showRoutes(교대역.getId(), 양재역.getId());

        final List<String> stationNames = pathResponse.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(pathResponse.getStations()).hasSize(3),
                () -> assertThat(stationNames).containsExactly("교대역", "남부터미널역", "양재역"),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않아서 조회가 불가능합니다.")
    @Test
    void error_notConnectedSourceNTarget() {

        assertThatThrownBy(() -> pathService.showRoutes(교대역.getId(), 검암역.getId()))
                .isInstanceOf(NoPathConnectedException.class)
                .hasMessage(NO_PATH_CONNECTED.getMessage());
    }

    @DisplayName("출발역과 도착역이 같아서 조회가 불가능합니다.")
    @Test
    void error_sameSourceNTarget() {

        assertThatThrownBy(() -> pathService.showRoutes(교대역.getId(), 교대역.getId()))
                .isInstanceOf(SameStationException.class)
                .hasMessage(NO_FIND_SAME_SOURCE_TARGET_STATION.getMessage());
    }

    @DisplayName("요청한 역이 노선의 등록되어 있지 않아서 조회가 불가능합니다.")
    @Test
    void error_noRegisterStation() {

        assertThatThrownBy(() -> pathService.showRoutes(교대역.getId(), 몽촌토성역.getId()))
                .isInstanceOf(NoRegisterStationException.class)
                .hasMessage(NO_REGISTER_LINE_STATION.getMessage());
    }

    private Station createStation(final String station) {
        return stationRepository.save(new Station(station));
    }

    private Line createLineBySections(final String name, final String color, final List<Section> sections) {
        return lineRepository.save(new Line(name, color, new Sections(sections)));
    }
}
