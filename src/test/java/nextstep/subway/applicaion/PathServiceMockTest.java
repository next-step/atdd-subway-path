package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.NotRegisteredInAllSectionsException;
import nextstep.subway.exception.SourceAndTargetNotLinkedException;
import nextstep.subway.exception.SourceAndTargetSameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

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
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

    }

    @DisplayName("경로를 조회할 수 있다.")
    @Test
    void findPath() {
        //given

        Long source = 1L;
        Long target = 3L;

        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));

        when(stationRepository.findById(source)).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(target)).thenReturn(Optional.of(양재역));

        //when
        PathResponse pathResponse = pathService.findPath(source, target);

        //then
        assertThat(pathResponse.getDistance()).isEqualTo(5);
        assertThat(toStationsNames(pathResponse.getStations())).containsExactly("교대역", "남부터미널역", "양재역");

    }


    @DisplayName("경로를 조회 시 출발역과 도착역이 같으면 에러 발생")
    @Test
    void findPathSourceAndTargetSameException() {
        //given
        Long source = 1L;
        when(stationRepository.findById(source)).thenReturn(Optional.of(교대역));

        //when, then
        assertThatThrownBy(() -> pathService.findPath(source, source))
                .isInstanceOf(SourceAndTargetSameException.class)
                .hasMessage("구간 조회 시 출발역과 도착역이 같을 수 없습니다.");

    }

    @DisplayName("경로를 조회 시 출발역과 도착역이 연결되지 않으면 에러 발생")
    @Test
    void findPathSourceAndTargetNotLinkedException() {
        //given

        Station 천호역 = new Station("천호역");
        Station 강동역 = new Station("강동역");

        Line 오호선 = new Line("오호선", "purple");
        오호선.addSection(new Section(오호선, 천호역, 강동역, 2));

        Long source = 1L;
        Long target = 5L;

        when(lineRepository.findAll()).thenReturn(List.of(이호선, 오호선));

        when(stationRepository.findById(source)).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(target)).thenReturn(Optional.of(천호역));


        //when, then
        assertThatThrownBy(() -> pathService.findPath(source, target))
                .isInstanceOf(SourceAndTargetNotLinkedException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");

    }

    @DisplayName("경로를 조회 시 출발역이 존재하지 않으면 에러 발생")
    @Test
    void findPathNotExistSourceException() {
        //given

        Station 천호역 = new Station("천호역");


        Long source = 1L;
        Long target = 5L;

        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));

        when(stationRepository.findById(source)).thenReturn(Optional.of(교대역));
        when(stationRepository.findById(target)).thenReturn(Optional.of(천호역));


        //when, then
        assertThatThrownBy(() -> pathService.findPath(source, target))
                .isInstanceOf(NotRegisteredInAllSectionsException.class)
                .hasMessage("출발역 또는 도착역이 존재하지 않습니다.");

    }


    @DisplayName("경로를 조회 시 도착역이 존재하지 않으면 에러 발생")
    @Test
    void findPathNotExistTargetException() {
        //given

        Station 천호역 = new Station("천호역");


        Long source = 1L;
        Long target = 5L;

        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));

        when(stationRepository.findById(source)).thenReturn(Optional.of(천호역));
        when(stationRepository.findById(target)).thenReturn(Optional.of(교대역));


        //when, then
        assertThatThrownBy(() -> pathService.findPath(source, target))
                .isInstanceOf(NotRegisteredInAllSectionsException.class)
                .hasMessage("출발역 또는 도착역이 존재하지 않습니다.");

    }

    private List<String> toStationsNames(List<StationResponse> stations) {
        return stations.stream()
                .map(stationResponse -> stationResponse.getName())
                .collect(Collectors.toList());
    }

}