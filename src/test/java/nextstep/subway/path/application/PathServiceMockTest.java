package nextstep.subway.path.application;

import nextstep.subway.exceptions.InvalidPathPointException;
import nextstep.subway.exceptions.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("PathService 클래스")
@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineRepository lineRepository;

    @Autowired
    private StationService stationService;

    @Autowired
    private PathService pathService;

    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    private Station 강남역;
    private Station 교대역;
    private Station 남부터미널역;
    private Station 양재역;
    private Station 범계역;

    @BeforeEach
    void setup() {
        stationService = new StationService(stationRepository);
        pathService = new PathService(stationRepository, lineRepository, stationService);

        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);

        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);

        교대역 = new Station("교대역");
        ReflectionTestUtils.setField(교대역, "id", 3L);

        남부터미널역 = new Station("남부터미널역");
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);

        범계역 = new Station("범계역");
        ReflectionTestUtils.setField(범계역, "id", 5L);


        신분당선 = new Line("신분당선", "bg-red-600", 강남역, 양재역, 7);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
        이호선 = new Line("이호선", "bg-red-600", 교대역, 강남역, 6);
        ReflectionTestUtils.setField(이호선, "id", 2L);
        삼호선 = new Line("삼호선", "bg-red-600", 교대역, 양재역, 5);
        ReflectionTestUtils.setField(삼호선, "id", 3L);

        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));
    }

    @Nested
    @DisplayName("searchShortestPath 메소드는")
    class Describe_searchShortestPath {

        @Nested
        @DisplayName("만약 출발역과 도착역이 같은경우")
        class Context_with_equals_start_and_end {
            @DisplayName("InvalidPathPointException이 발생한다.")
            @Test
            void it_is_invalidPathPointException() {
                //when, then
                assertThatThrownBy(()->{
                    pathService.searchShortestPath(범계역.getId(), 범계역.getId());
                }).isInstanceOf(InvalidPathPointException.class);
            }
        }

        @Nested
        @DisplayName("만약 출발역과 도착역이 연결이 되어 있지 않은 경우")
        class Context_with_not_connected_station {
            @DisplayName("NPE 문제가 발생한다.")
            @Test
            void is_is_NPException() {
                //given
                when(stationRepository.findById(범계역.getId())).thenReturn(ofNullable(범계역));
                when(stationRepository.findById(교대역.getId())).thenReturn(ofNullable(교대역));
                when(stationRepository.findAll()).thenReturn(Arrays.asList(강남역, 교대역, 남부터미널역, 양재역, 범계역));
                when(lineRepository.findAll()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

                //when, then
                assertThatThrownBy(()->{
                    pathService.searchShortestPath(범계역.getId(), 교대역.getId());
                }).isInstanceOf(NullPointerException.class);
            }

        }

        @Nested
        @DisplayName("존재하지 않는 출발역이나 도착역을 조회 할 경우")
        class Context_with_invalid_station {
            @DisplayName("NotFoundStation Exception 이 발생한다.")
            @Test
            void it_is_exception_from_not_exists_station() {
                //given
                when(stationRepository.findById(5L)).thenReturn(Optional.empty());
                when(stationRepository.findAll()).thenReturn(Arrays.asList(강남역, 교대역, 남부터미널역, 양재역));
                when(lineRepository.findAll()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

                //when, then
                assertThatThrownBy(()->{
                    pathService.searchShortestPath(5L, 교대역.getId());
                }).isInstanceOf(NotFoundStationException.class);

                assertThatThrownBy(()->{
                    pathService.searchShortestPath(양재역.getId(), 5L);
                }).isInstanceOf(NotFoundStationException.class);
            }

        }

        @Nested
        @DisplayName("만약 존재하며 연결된 지하철역을 조회할 경우 ")
        class Context_with_valid_station {
            @DisplayName("정상적으로 최단 거리와 거리내에 포함된 지하철역을 반환한다.")
            @Test
            void it_is_search_from_valid_parameter() {
                //given
                when(stationRepository.findById(양재역.getId())).thenReturn(ofNullable(양재역));
                when(stationRepository.findById(교대역.getId())).thenReturn(ofNullable(교대역));
                when(stationRepository.findAll()).thenReturn(Arrays.asList(강남역, 교대역, 남부터미널역, 양재역));
                when(lineRepository.findAll()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

                //when
                PathResponse pathResponse = pathService.searchShortestPath(양재역.getId(), 교대역.getId());

                //then
                List<StationResponse> stationResponses = Stream.of(양재역, 남부터미널역, 교대역)
                        .map(StationResponse::of)
                        .collect(Collectors.toList());

                assertThat(pathResponse.getStations()).hasSize(3);
                assertThat(pathResponse.getDistance()).isEqualTo(5.0);
                assertThat(pathResponse.getStations()).containsExactlyElementsOf(stationResponses);
            }

        }

    }



}
