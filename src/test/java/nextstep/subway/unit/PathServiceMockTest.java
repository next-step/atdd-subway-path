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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("경로 서비스(PathService)")
@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
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

        이호선 = new Line(LineFixture.이호선, LineFixture.초록색);
        이호선.addSection(new Section(이호선, 교대역, 강남역, LineFixture.교대_강남_거리));

        신분당선 = new Line(LineFixture.신분당선, LineFixture.빨강색);
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, LineFixture.강남_양재_거리));

        삼호선 = new Line(LineFixture.삼호선, LineFixture.오렌지색);
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, LineFixture.교대_남부터미널_거리));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, LineFixture.남부터미널_양재_거리));
    }

    @DisplayName("출발역과 도착역간의 최단 거리 조회")
    @Test
    void findShortestPath() {
        // given
        final Long sourceId = 1L;
        final Long targetId = 2L;

        given(stationRepository.findById(sourceId)).willReturn(Optional.of(교대역));
        given(stationRepository.findById(targetId)).willReturn(Optional.of(양재역));
        given(lineRepository.findAll()).willReturn(List.of(이호선, 신분당선, 삼호선));

        // when
        final ShortestPathResponse shortestPathResponse = pathService.findShortestPath(sourceId, targetId);

        // then
        assertAll(
                () -> assertThat(shortestPathResponse.getStations().size()).isEqualTo(3),
                () -> assertThat(shortestPathResponse.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 같은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathBySameSourceAndTarget() {
        // given
        given(stationRepository.findById(any())).willReturn(Optional.of(교대역));

        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("source and target stations were conflict");
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoLinkedSourceAndTarget() {
        // given
        final Long sourceId = 1L;
        final Long targetId = 2L;
        final Station 광명역 = new Station("광명역");

        given(stationRepository.findById(sourceId)).willReturn(Optional.of(교대역));
        given(stationRepository.findById(targetId)).willReturn(Optional.of(광명역));
        given(lineRepository.findAll()).willReturn(List.of(이호선, 신분당선, 삼호선));

        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(sourceId, targetId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 출발역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceSource() {
        // given
        final Long sourceId = -1L;
        final Long targetId = 1L;

        given(stationRepository.findById(sourceId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(sourceId, targetId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 도착역을 조회 할 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoExistenceTarget() {
        // given
        final Long sourceId = 1L;
        final Long targetId = -1L;

        given(stationRepository.findById(sourceId)).willReturn(Optional.of(교대역));
        given(stationRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pathService.findShortestPath(sourceId, targetId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
