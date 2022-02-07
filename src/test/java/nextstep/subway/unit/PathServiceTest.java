package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.fixture.LineFixture;
import nextstep.subway.fixture.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class PathServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station(StationFixture.교대역);
        강남역 = new Station(StationFixture.강남역);
        양재역 = new Station(StationFixture.양재역);
        남부터미널역 = new Station(StationFixture.남부터미널역);

        stationRepository.save(교대역);
        stationRepository.save(강남역);
        stationRepository.save(양재역);
        stationRepository.save(남부터미널역);

        이호선 = new Line(LineFixture.이호선, LineFixture.초록색);
        이호선.addSection(new Section(이호선, 교대역, 강남역, LineFixture.교대_강남_거리));

        신분당선 = new Line(LineFixture.신분당선, LineFixture.빨강색);
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, LineFixture.강남_양재_거리));

        삼호선 = new Line(LineFixture.삼호선, LineFixture.오렌지색);
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, LineFixture.교대_남부터미널_거리));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, LineFixture.남부터미널_양재_거리));

        lineRepository.save(이호선);
        lineRepository.save(신분당선);
        lineRepository.save(삼호선);
    }

    @DisplayName("출발역과 도착역간의 최단 거리 조회")
    @Test
    void findShortestPath() {
        // when
        final ShortestPathResponse shortestPathResponse = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        // then
        assertAll(
                () -> assertThat(shortestPathResponse.getStations().size()).isEqualTo(3),
                () -> assertThat(shortestPathResponse.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 같은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathBySameSourceAndTarget() {
        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(교대역.getId(), 교대역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("source and target stations were conflict");
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoLinkedSourceAndTarget() {
        // given
        final Station 광명역 = new Station("광명역");
        stationRepository.save(광명역);

        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(교대역.getId(), 광명역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 출발역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceSource() {
        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(-1L, 교대역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 도착역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceTarget() {
        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(교대역.getId(), -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
