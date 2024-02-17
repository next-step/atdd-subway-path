package nextstep.subway.unit;

import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.domain.response.StationResponse;
import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.service.LineService;
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

    @Mock
    private LineService lineService;

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
        // given: 역 정보와 노선 정보가 주어진다.
        when(stationService.findById(교대역ID))
                .thenReturn(교대역);
        when(stationService.findById(양재역ID))
                .thenReturn(양재역);
        when(sectionRepository.findAll())
                .thenReturn(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재));

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
        Long source = 교대역ID;
        Long target = 교대역ID;

        // when, then
        assertThatThrownBy(() -> pathService.findShortestPath(source, target))
                .isEqualTo(ExceptionMessage.SAME_SOURCE_TARGET_EXCEPTION.getMessage());
    }

}
