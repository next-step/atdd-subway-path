package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PathServiceTest {

    @Autowired
    private PathService pathService;
    @Autowired
    LineService lineService;

    @Autowired
    StationRepository stationRepository;


    private Long 고속터미널역;
    private Long 구반포역;
    private Long 이촌역;
    private Long 동작역;
    private Long 이수역;
    private Long 사당역;
    private Long 교대역;

    private Long 사호선;
    private Long 삼호선;
    private Long 이호선;
    private Long 신분당선;
    private Long 구호선;


    /**
     * 이촌역              고속터미널역
     * |                      |
     * 4호선(2)             3호선(2)
     * |                      |
     * 동작역 -- 9호선(2) -- 구반포역
     * |                      |
     * 4호선(2)                |
     * |                      |
     * 이수역               신분당선(1)
     * |                      |
     * 4호선(3)                |
     * |                      |
     * 사당역 --  2호선(10) -- 교대역
     */
    @BeforeEach
    void setUp() {
        고속터미널역 = createStation("고속터미널역");
        구반포역 = createStation("구반포역");
        삼호선 = createLine("3호선", "orange", 고속터미널역, 구반포역, 2);


        이촌역 = createStation("이촌역");
        동작역 = createStation("동작역");
        사호선 = createLine("4호선", "orange", 이촌역, 동작역, 2);

        이수역 = createStation("이수역");
        사당역 = createStation("사당역");
        addSection(사호선, 동작역, 이수역, 2);
        addSection(사호선, 이수역, 사당역, 3);

        교대역 = createStation("교대역");
        이호선 = createLine("2호선", "green", 사당역, 교대역, 10);

        신분당선 = createLine("신분당선", "red", 교대역, 구반포역, 1);
        구호선 = createLine("9호선", "brown", 구반포역, 동작역, 2);
    }

    @Test
    @DisplayName("최단거리를 반환한다.")
    void getPathsTest() {
        PathResponse pathResponse = pathService.getPaths(교대역, 사당역);

        List<Long> stations = pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toUnmodifiableList());
        assertThat(stations).containsExactly(교대역, 구반포역, 동작역, 이수역, 사당역);
        assertThat(pathResponse.getDistance()).isEqualTo(8);
    }

    @Test
    @DisplayName("상행선 & 하행선 관계없이 최단거리를 반환한다.")
    void getPathsTest2() {
        PathResponse pathResponse = pathService.getPaths(고속터미널역, 이촌역);

        List<Long> stations = pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toUnmodifiableList());
        assertThat(stations).containsExactly(고속터미널역, 구반포역, 동작역, 이촌역);
        assertThat(pathResponse.getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("연결되지 않은 역을 찾으면 에러가 발생한다.")
    void getPathsFailTest() {
        Long 신규역 = createStation("신규역");
        assertThatIllegalArgumentException().isThrownBy(() -> pathService.getPaths(신규역, 이촌역))
                .withMessage("경로를 찾을 수 없어요.");
    }

    @Test
    @DisplayName("출발역이 없는 역을 찾으면 에러가 발생한다.")
    void getPathsNotExistSourceStationFailTest() {
        Long 없는역 = -1L;
        assertThatIllegalArgumentException().isThrownBy(() -> pathService.getPaths(없는역, 이촌역))
                .withMessage("출발역이 존재하지 않아요.");
    }

    @Test
    @DisplayName("도착역이 없는 역을 찾으면 에러가 발생한다.")
    void getPathsNotExistTargetStationFailTest() {
        Long 없는역 = -1L;
        assertThatIllegalArgumentException().isThrownBy(() -> pathService.getPaths(이촌역, 없는역))
                .withMessage("도착역이 존재하지 않아요.");
    }

    @Test
    @DisplayName("도착역이 없는 역을 찾으면 에러가 발생한다.")
    void getPathsSameStationFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> pathService.getPaths(이촌역, 이촌역))
                .withMessage("같은역은 올 수 없어요.");
    }

    private Long createStation(String stationName) {
        return stationRepository.save(new Station(stationName)).getId();
    }

    private Long createLine(String name, String orange, Long upStationId, Long downStationId, int distance) {
        return lineService.saveLine(new LineRequest(name, orange, upStationId, downStationId, distance)).getId();
    }

    private void addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));
    }

}