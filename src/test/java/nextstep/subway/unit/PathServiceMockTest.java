package nextstep.subway.unit;

import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.domain.response.StationResponse;
import nextstep.subway.exception.ApplicationException;
import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.service.PathService;
import nextstep.subway.service.StationService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {


    @Mock
    private StationService stationService;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private PathService pathService;

    private Long 교대역ID = 1L;
    private Long 강남역ID = 2L;
    private Long 양재역ID = 3L;
    private Long 남부터미널역ID = 4L;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Section 교대_강남;
    private Section 강남_양재;
    private Section 교대_남부터미널;
    private Section 남부터미널_양재;


    /**
     * 교대역    --- *2호선(10)* ---   강남역
     * |                              |
     * *3호선(2)*                   *신분당선(10)*
     * |                              |
     * 남부터미널역  --- *3호선(3)* ---   양재
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station(교대역ID, "교대역");
        강남역 = new Station(강남역ID, "강남역");
        양재역 = new Station(양재역ID, "양재역");
        남부터미널역 = new Station(남부터미널역ID, "남부터미널역");

        교대_강남 = new Section(교대역, 강남역, 10);
        강남_양재 = new Section(강남역, 양재역, 10);
        교대_남부터미널 = new Section(교대역, 남부터미널역, 2);
        남부터미널_양재 = new Section(남부터미널역, 양재역, 3);
    }

    @DisplayName("최단 경로 탐색")
    @Test
    void findShortestPath() {
        // given
        when(stationService.findById(교대역ID)).thenReturn(교대역);
        when(stationService.findById(양재역ID)).thenReturn(양재역);
        when(sectionRepository.findAll()).thenReturn(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재));

        // when: 출발역 id와 도착역 id를 받으면 최단경로를 반환한다.
        PathResponse pathResponse = pathService.findShortestPath(교대역ID, 양재역ID);
        List<String> stationNames = pathResponse.getStationList().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(stationNames).containsExactly("교대역", "남부터미널역", "양재역"),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void findShortestPathException1() {
        // given
        when(stationService.findById(교대역ID))
                .thenReturn(교대역);

        // when, then
        Long source = 교대역ID;
        Long target = 교대역ID;
        assertThatThrownBy(() ->  pathService.findShortestPath(source, target))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ExceptionMessage.SAME_SOURCE_TARGET_EXCEPTION.getMessage());
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않으면 예외처리")
    @Test
    void findShortestPathException2() {
        // given
        Station 사당역 = new Station(5L, "사당역");
        Station 이수역 = new Station(6L, "이수역");
        Section 사당_이수 = new Section(사당역, 이수역, 10);

        when(stationService.findById(교대역ID)).thenReturn(교대역);
        when(stationService.findById(5L)).thenReturn(사당역);
        when(sectionRepository.findAll()).thenReturn(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재, 사당_이수));

        // when, then
        Long source = 5L; // 사당역 ID
        Long target = 교대역ID;
        assertThatThrownBy(() ->  pathService.findShortestPath(source, target))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ExceptionMessage.NOT_CONNECTED_EXCEPTION.getMessage());
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외처리")
    @Test
    void findShortestPathException3() {
        // given
        Station 사당역 = new Station(5L, "사당역");

        when(stationService.findById(교대역ID)).thenReturn(교대역);
        when(stationService.findById(5L)).thenReturn(사당역);
        when(sectionRepository.findAll()).thenReturn(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재));

        // when, then
        Long source = 5L; // 사당역 ID
        Long target = 교대역ID;
        assertThatThrownBy(() ->  pathService.findShortestPath(source, target))
                .isInstanceOf(ApplicationException.class)
                .hasMessage(ExceptionMessage.NO_EXISTS_STATION_EXCEPTION.getMessage());
    }

}
