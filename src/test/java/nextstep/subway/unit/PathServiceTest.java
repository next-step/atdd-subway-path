package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

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

        이호선 = new Line("2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @Mock
    private LineRepository lineRepository;
    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    @DisplayName("")
    @Test
    void findPath() {
        //Given
        lineService = new LineService(lineRepository, stationService);
        PathService pathService = new PathService(lineService, stationService);
        when(stationService.findById(1L)).thenReturn(교대역);
        when(stationService.findById(3L)).thenReturn(양재역);
        when(lineService.findAllLine()).thenReturn(List.of(이호선, 신분당선, 삼호선));

        //When
        PathResponse response = pathService.findPath(1L, 3L);

        //Then
        assertThat(response.getDistance()).isEqualTo(5);
    }
}
