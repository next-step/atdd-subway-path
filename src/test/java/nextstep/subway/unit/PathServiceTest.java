package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;

    private Station 교대역;
    private Station 강남역;
    private Station 남터역;
    private Station 양재역;
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        교대역 = createStation(1L, "교대역");
        강남역 = createStation(2L, "강남역");
        남터역 = createStation(3L, "남터역");
        양재역 = createStation(4L, "양재역");

        이호선 = Line.of("이호선", "green");
        삼호선 = Line.of("삼호선", "orange");
        신분당선 = Line.of("신분당선", "red");

        이호선.addSection(Section.of(이호선, 교대역, 강남역, 10));
        삼호선.addSection(Section.of(삼호선, 교대역, 남터역, 10));
        신분당선.addSection(Section.of(삼호선, 강남역, 양재역, 10));
    }


    @Test
    void shortest() {
        //given
        when(stationService.findById(남터역.getId())).thenReturn(남터역);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);
        when(lineService.findAllLines()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));

        PathService pathService = new PathService(lineService, stationService);

        //when
        PathResponse shortest = pathService.findShortestPath(양재역.getId(), 남터역.getId());
        List<String> namesOfStations = shortest.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        //then
        assertThat(shortest.getDistance()).isEqualTo(30);
        assertThat(namesOfStations).containsExactly(양재역.getName(), 강남역.getName(), 교대역.getName(), 남터역.getName());

    }

    private Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}