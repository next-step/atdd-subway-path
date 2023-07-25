package nextstep.subway.unit;

import nextstep.marker.MockitoUnitTest;
import nextstep.subway.common.NotFoundStationException;
import nextstep.subway.controller.resonse.PathResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.PathFindService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@MockitoUnitTest
public class PathFindServiceTest {

    @Mock
    private StationRepository stationRepository;


    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private PathFindService pathFindService;

    private Station 교대역;
    private Long 교대역Id;
    private Station 강남역;
    private Long 강남역Id;
    private Station 양재역;
    private Long 양재역Id;
    private Station 남부터미널역;
    private Long 남부터미널역Id;
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
    void setUp() {
        교대역Id = 1L;
        교대역 = getStation();

        강남역Id = 2L;
        강남역 = getStation();

        양재역Id = 3L;
        양재역 = getStation();

        남부터미널역Id = 4L;
        남부터미널역 = getStation();

        이호선 = getSavedLine();
        신분당선 = getSavedLine();
        삼호선 = getSavedLine();
    }


    @Nested
    class Success {

        @Test
        void 교대역에서_양재역을_가는_최단_경로는_교대_남부터미널_양재_9미터이다() {
            // given
            given(stationRepository.findById(교대역Id)).willReturn(Optional.of(교대역));
            given(교대역.getId()).willReturn(교대역Id);
            given(stationRepository.findById(양재역Id)).willReturn(Optional.of(양재역));
            given(양재역.getId()).willReturn(양재역Id);
            given(남부터미널역.getId()).willReturn(남부터미널역Id);
            givenLines();

            // when
            PathResponse shortestPath = pathFindService.getShortestPath(교대역Id, 양재역Id);

            // then
            verifyShortestPathResponse(shortestPath, 9L, 교대역Id, 남부터미널역Id, 양재역Id);
        }

        @Test
        void 강남역에서_남부터미널역을_가는_최단_경로는_강남_교대_남부터미널_12미터이다() {
            // given
            given(stationRepository.findById(강남역Id)).willReturn(Optional.of(강남역));
            given(강남역.getId()).willReturn(강남역Id);
            given(stationRepository.findById(남부터미널역Id)).willReturn(Optional.of(남부터미널역));
            given(남부터미널역.getId()).willReturn(남부터미널역Id);
            given(교대역.getId()).willReturn(교대역Id);
            givenLines();

            // when
            PathResponse shortestPath = pathFindService.getShortestPath(강남역Id, 남부터미널역Id);

            // then
            verifyShortestPathResponse(shortestPath, 12L, 강남역Id, 교대역Id, 남부터미널역Id);
        }

        @Test
        void 강남역에서_양재역을_가는_최단_경로는_강남_양재_10미터이다() {
            // given
            given(stationRepository.findById(강남역Id)).willReturn(Optional.of(강남역));
            given(강남역.getId()).willReturn(강남역Id);
            given(stationRepository.findById(양재역Id)).willReturn(Optional.of(양재역));
            given(양재역.getId()).willReturn(양재역Id);
            givenLines();

            // when
            PathResponse shortestPath = pathFindService.getShortestPath(강남역Id, 양재역Id);

            // then
            verifyShortestPathResponse(shortestPath, 10L, 강남역Id, 양재역Id);
        }

        private void givenLines() {
            given(이호선.getSections()).willReturn(Collections.singletonList(Section.of(교대역, 강남역, 5L)));
            given(삼호선.getSections()).willReturn(List.of(Section.of(교대역, 남부터미널역, 7L), Section.of(남부터미널역, 양재역, 2L)));
            given(신분당선.getSections()).willReturn(List.of(Section.of(강남역, 양재역, 10L)));
            given(lineRepository.findAll()).willReturn(List.of(이호선, 신분당선, 삼호선));
        }

        private void verifyShortestPathResponse(PathResponse pathResponse, long distance, Long... stationIds) {
            Assertions.assertEquals(distance, pathResponse.getDistance());
            assertThat(pathResponse.getStationResponses()).hasSize(stationIds.length)
                    .map(StationResponse::getId)
                    .containsExactly(stationIds);
        }

    }

    @Nested
    class Fail {

        @Test
        void 출발역과_도착역이_같은_경우() {
            // given
            given(stationRepository.findById(강남역Id)).willReturn(Optional.of(강남역));

            // when & then
            thenCode(() -> pathFindService.getShortestPath(강남역Id, 강남역Id)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 출발역과_도착역이_연결되지_않은_경우() {
            // given
            Station 다른역 = getStation();
            given(stationRepository.findById(강남역Id)).willReturn(Optional.of(강남역));
            given(stationRepository.findById(6L)).willReturn(Optional.of(다른역));

            // when & then
            thenCode(() -> pathFindService.getShortestPath(강남역Id, 6L)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 출발역과_도착역이_존재하지_않는_경우() {
            // given
            given(stationRepository.findById(8L)).willReturn(Optional.empty());

            // when & then
            thenCode(() -> pathFindService.getShortestPath(8L, 남부터미널역Id)).isInstanceOf(NotFoundStationException.class);
        }
    }

    private Line getSavedLine() {
        Line line = mock(Line.class);
        return line;
    }

    private Station getStation() {
        Station station = mock(Station.class);
        return station;
    }
}
