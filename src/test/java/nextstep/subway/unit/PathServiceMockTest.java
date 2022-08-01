package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    LineRepository lineRepository;
    @Mock
    StationRepository stationRepository;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;
    List<Line> lineList;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line("2호선", "green");
        삼호선 = new Line("3호선", "orange");
        신분당선 = new Line("신분당선", "red");

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 10));
        신분당선.addSection(new Section(신분당선, 교대역, 남부터미널역, 10));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        lineList = Arrays.asList(이호선, 삼호선, 신분당선);
    }

    @Test
    void getPaths() {
        when(lineRepository.findAll()).thenReturn(lineList);
        when(stationRepository.findById(교대역.getId())).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(양재역.getId())).thenReturn(Optional.of(양재역));

        PathService pathService = new PathService(stationRepository, lineRepository);
        PathResponse pathResponse = pathService.getPath(new PathRequest(교대역.getId(), 양재역.getId()));

        assertThat(pathResponse.getStations()).hasSize(3);
        assertThat(pathResponse.getStations()
            .stream()
            .map(station -> station.getId())
            .collect(Collectors.toList())).containsExactly(교대역.getId(), 남부터미널역.getId(),
            양재역.getId());
    }
}
