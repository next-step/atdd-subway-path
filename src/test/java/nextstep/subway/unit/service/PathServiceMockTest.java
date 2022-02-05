package nextstep.subway.unit.service;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.NotFoundStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.unit.PathFinderTestHelper.STATIONS_SIZE_YANGJAE_TO_YEOKSAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private PathService pathService;

    private List<Line> lines = new ArrayList<>();
    private PathRequest pathRequest;
    private Station 역삼역;
    private Station 양재역;
    private Station 삼성역;
    private Station 석촌역;

    @BeforeEach
    void setFixtures() {
        givens();
    }

    @Test
    void 최단_경로_조회() {
        when(lineRepository.findAll()).thenReturn(lines);
        when(stationService.findStationById(양재역.getId())).thenReturn(양재역);
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        PathResponse response = pathService.findShortestPath(pathRequest);
        assertThat(response.getStations()).hasSize(STATIONS_SIZE_YANGJAE_TO_YEOKSAM);
    }

    private void givens() {
        Station 교대역 = new Station("교대역");
        Station 강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        Station 남부터미널역 = new Station("남부터미널역");
        양재역 = new Station("양재역");
        삼성역 = new Station("삼성역");
        석촌역 = new Station("석촌역");
        Station 단대오거리역 = new Station("단대오거리역");

        ReflectionTestUtils.setField(교대역, "id", 1L);
        ReflectionTestUtils.setField(강남역, "id", 2L);
        ReflectionTestUtils.setField(역삼역, "id", 3L);
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);
        ReflectionTestUtils.setField(양재역, "id", 5L);
        ReflectionTestUtils.setField(삼성역, "id", 6L);
        ReflectionTestUtils.setField(석촌역, "id", 7L);
        ReflectionTestUtils.setField(단대오거리역, "id", 8L);

        Line 신분당선 = new Line("신분당선", "bg-red-600");
        Line 이호선 = new Line("2호선", "bg-green-600");
        Line 삼호선 = new Line("3호선", "bg-orange-600");
        Line 팔호선 = new Line("8호선", "bg-pink-600");

        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 5));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 2));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 4));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
        팔호선.addSection(new Section(팔호선, 석촌역, 단대오거리역, 5));

        lines.add(신분당선);
        lines.add(이호선);
        lines.add(삼호선);
        lines.add(팔호선);

        pathRequest = new PathRequest();
        ReflectionTestUtils.setField(pathRequest, "source", 5L);
        ReflectionTestUtils.setField(pathRequest, "target", 3L);
    }
}
