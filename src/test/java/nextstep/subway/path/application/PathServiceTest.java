package nextstep.subway.path.application;

import com.google.common.collect.Lists;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    @Autowired
    private PathService pathService;

    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;
    private Station 강남역;
    private Station  양재역;
    private Station 교대역;
    private Station 남부터미널역;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineService, stationService);

        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);
        교대역 = new Station("교대역");
        ReflectionTestUtils.setField(교대역, "id", 3L);
        남부터미널역 = new Station("남부터미널역");
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);

        신분당선 = new Line("신분당선", "bg-red-600", 강남역, 양재역, 10);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
        이호선 = new Line("이호선", "bg-red-600", 교대역, 강남역, 10);
        ReflectionTestUtils.setField(이호선, "id", 2L);
        삼호선 = new Line("삼호선", "bg-red-600", 교대역, 양재역, 5);
        ReflectionTestUtils.setField(이호선, "id", 3L);

        삼호선.addSection(삼호선, 교대역, 남부터미널역, 3);
    }

    @Test
    void getShortestPathTest(Long sourceId, Long targetId) {
        // given
        when(lineService.findLines()).thenReturn(Lists.newArrayList(신분당선, 이호선, 삼호선));
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(남부터미널역.getId())).thenReturn(남부터미널역);

        // when
        PathResponse pathResponse= pathService.getShortestPath(강남역.getId(), 남부터미널역.getId());

        // then
        assertThat(pathResponse.getStations()).isEqualTo(Arrays.asList(강남역, 교대역, 남부터미널역));
        assertThat(pathResponse.getDistance()).isEqualTo(13);
    }
}
