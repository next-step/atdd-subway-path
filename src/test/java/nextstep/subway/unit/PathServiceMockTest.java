package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("최단 경로 찾기 테스트")
@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    Station 강남역;
    Station 교대역;
    Station 삼성역;
    Line 이호선;
    Line 신분당선;
    Line 삼호선;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
        삼성역 = new Station("삼성역");

        이호선 = new Line("2호선", "green");
        이호선.getSections().add(new Section(이호선, 강남역, 교대역, 5));

        신분당선 = new Line("신분당선", "red");
        신분당선.getSections().add(new Section(신분당선, 교대역, 삼성역, 2));

        삼호선 = new Line("3호선", "orange");
        삼호선.getSections().add(new Section(삼호선, 강남역, 삼성역, 12));
    }

    @Test
    @DisplayName("Stub - 최단 경로 찾기 서비스 레이어 테스트")
    void showShortestPath() {
        // lineAll.stream.map(Line::getStations).collect(toSet());
        // lineAll.stream.map(Line::getSections).collect(toSet());

        //given
        PathService pathService = new PathService(lineRepository, stationRepository, pathFinder);
        when(lineRepository.findAll()).thenReturn(Arrays.asList(이호선, 신분당선, 삼호선));
        when(stationRepository.findById(강남역.getId())).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(삼성역.getId())).thenReturn(Optional.of(삼성역));
        when(pathFinder.shortestPath(any(), any(), any()))
                .thenReturn(new PathResponse(Arrays.asList(new PathStationResponse(), new PathStationResponse(), new PathStationResponse()), 7));


        //when
        PathResponse pathResponse = pathService.showShortestPath(강남역.getId(), 삼성역.getId());

        //then
        assertThat(pathResponse.getDistance()).isEqualTo(7);
        assertThat(pathResponse.getStations()).hasSize(3);
    }
}
