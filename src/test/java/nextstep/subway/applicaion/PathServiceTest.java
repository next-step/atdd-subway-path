package nextstep.subway.applicaion;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("경로 서비스(PathService)")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

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

        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("출발역과 도착역이 같은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathBySameSourceAndTarget() {
        given(stationRepository.findById(any())).willReturn(Optional.of(교대역));

        assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("source and target stations were conflict");
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우의 최단 경로와 거리 조회")
    @Test
    void findShortestPathByNoLinkedSourceAndTarget() {
        final Long sourceId = 1L;
        final Long targetId = 2L;
        final Station 광명역 = new Station("광명역");

        given(stationRepository.findById(sourceId)).willReturn(Optional.of(교대역));
        given(stationRepository.findById(targetId)).willReturn(Optional.of(광명역));
        given(lineRepository.findAll()).willReturn(List.of(이호선, 신분당선, 삼호선));

        assertThatThrownBy(() -> pathService.findShortestPath(sourceId, targetId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("source and target stations were not Linked");
    }
}
