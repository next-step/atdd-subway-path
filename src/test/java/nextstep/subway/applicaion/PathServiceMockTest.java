package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

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


    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("이호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("삼호선", "orange");
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));

    }

    @DisplayName("경로를 조회할 수 있다.")
    @Test
    void findPath() {
        //given

        Long source = 1L;
        Long target = 3L;

        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));

        //when
        PathResponse pathResponse = pathService.findPath(source, target);

        //then
        assertThat(pathResponse.getDistance()).isEqualTo(5);
        assertThat(toIdAndNamePairs(pathResponse.getStations()))
                .containsExactly(
                        Pair.of(1L, "교대역"),
                        Pair.of(4L, "남부터미널역"),
                        Pair.of(3L, "양재역")
                );

    }

    private List<Pair<Long, String>> toIdAndNamePairs(List<StationResponse> stations) {
        return stations.stream()
                .map(stationResponse -> Pair.of(stationResponse.getId(), stationResponse.getName()))
                .collect(Collectors.toList());
    }

}