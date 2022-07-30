package nextstep.subway.applicaion;

import static nextstep.subway.unit.LineUnitSteps.노선_구간_추가;
import static nextstep.subway.unit.StationUnitSteps.역_추가;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineSaveRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.NotConnectedException;
import nextstep.subway.exception.SameStationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private PathService pathService;

    long 교대역;
    long 강남역;
    long 남부터미널역;
    long 양재역;

    @BeforeEach
    void setUp() {
        교대역 = 역_생성("교대역");
        강남역 = 역_생성("강남역");
        남부터미널역 = 역_생성("남부터미널역");
        양재역 = 역_생성("양재역");

        노선_생성("이호선", "green", 교대역, 강남역, 10);
        노선_생성("삼호선", "orange", 교대역, 남부터미널역, 3);
        노선_생성("신분당선", "red", 강남역, 양재역, 5);
    }


    @Test
    @DisplayName("최단거리의 경로와 거리를 구한다.")
    void shortestPath() {
        //when
        final PathResponse shortestPath = pathService.getShortestPath(강남역, 남부터미널역);

        //then
        최단거리_경로_거리_검증(shortestPath);
    }

    @Test
    @DisplayName("출발역과 도착역이 같으면 예외")
    void sameStationsException() {
        assertThatExceptionOfType(SameStationException.class)
            .isThrownBy(() -> pathService.getShortestPath(강남역, 강남역))
            .withMessage("출발역과 도착역이 동일합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 역을 조회했을 시 예외")
    void notFoundException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> pathService.getShortestPath(Long.MAX_VALUE, 강남역));
    }

    @Test
    @DisplayName("연결되어 있지 않은 역을 조회할 경우 예외")
    void notConnectedException() {
        long 까치울역 = 역_생성("까치울역");
        long 강남구청역 = 역_생성("강남구청역");
        노선_생성("7호선", "red", 까치울역, 강남구청역, 5);
        assertThatExceptionOfType(NotConnectedException.class)
            .isThrownBy(() -> pathService.getShortestPath(까치울역, 강남역))
            .withMessage("입력한 두개의 역이 연결되어 있지 않습니다.");
    }

    private void 최단거리_경로_거리_검증(final PathResponse shortestPath) {
        assertAll(
            () -> assertThat(shortestPath.getStations()).hasSize(3)
                .containsExactly(
                    new StationResponse(강남역, "강남역"),
                    new StationResponse(교대역, "교대역"),
                    new StationResponse(남부터미널역, "남부터미널역")
                ),
            () -> assertThat(shortestPath.getDistance()).isEqualTo(13)
        );
    }

    private LineResponse 노선_생성(final String name, final String color, final long upStationId, final long downStationId
        , final int distance) {
        LineSaveRequest request = new LineSaveRequest(name, color, upStationId, downStationId, distance);
        return lineService.saveLine(request);
    }

    private long 역_생성(final String stationName) {
        final Station station = stationRepository.save(new Station(stationName));
        return station.getId();
    }
}