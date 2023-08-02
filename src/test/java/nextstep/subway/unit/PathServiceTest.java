package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.FindPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @InjectMocks
    private PathService pathService;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    Section 강남_양재_구간;
    Section 교대_강남_구간;
    Section 교대_남부터미널_구간;
    Section 남부터미널_양재_구간;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 10);
        신분당선.getSections().add(강남_양재_구간);

        이호선 = new Line("이호선", "green");
        교대_강남_구간 = new Section(이호선, 교대역, 강남역, 10);
        이호선.getSections().add(교대_강남_구간);

        삼호선 = new Line("이호선", "orange");
        교대_남부터미널_구간 = new Section(삼호선, 교대역, 남부터미널역, 2);
        남부터미널_양재_구간 = new Section(삼호선, 남부터미널역, 양재역, 3);
        삼호선.getSections().add(교대_남부터미널_구간);
        삼호선.getSections().add(남부터미널_양재_구간);
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경로 조회 실패")
    void findPath_sameSourceAndTarget() {
        assertThatThrownBy(() -> {
            pathService.find(stationIds.get(교대역), stationIds.get(교대역));
        }).isInstanceOf(FindPathException.class)
                .hasMessage(ErrorType.SAME_SOURCE_AND_TARGET.getMessage());
    }

    @Test
    @DisplayName("출발역이 존재하지 않는 경우 조회 실패")
    void findPath_NotExistSourceStations() {
        when(stationService.findById(0L)).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            pathService.find(0L, 999L);
        }).isInstanceOf(FindPathException.class)
                .hasMessage(ErrorType.NOT_EXIST_SOURCE_AND_TARGET.getMessage());
    }

    @Test
    @DisplayName("도착역이 존재하지 않는 경우 조회 실패")
    void findPath_NotExistTargetStations() {
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(stationService.findById(999L)).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            pathService.find(stationIds.get(강남역), 999L);
        }).isInstanceOf(FindPathException.class)
                .hasMessage(ErrorType.NOT_EXIST_SOURCE_AND_TARGET.getMessage());
    }

    @Test
    @DisplayName("최단 경로 조회")
    void findPath() {
        // given
        when(stationService.findById(stationIds.get(교대역))).thenReturn(교대역);
        when(stationService.findById(stationIds.get(양재역))).thenReturn(양재역);
        when(stationService.findAllStations()).thenReturn(List.of(교대역, 강남역, 양재역, 남부터미널역));
        when(lineService.findAllLines()).thenReturn(List.of(이호선, 삼호선, 신분당선));

        // when
        Path path = pathService.find(stationIds.get(교대역), stationIds.get(양재역));

        // then
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("출발역과 도착역을 연결하는 경로가 없는 경우 조회 실패")
    void findPath_NotExistPath() {
        // given
        when(stationService.findById(stationIds.get(교대역))).thenReturn(교대역);
        when(stationService.findById(stationIds.get(신사역))).thenReturn(신사역);
        when(stationService.findAllStations()).thenReturn(List.of(교대역, 강남역, 양재역, 남부터미널역, 신사역));
        when(lineService.findAllLines()).thenReturn(List.of(이호선, 삼호선, 신분당선));

        // when
        assertThatThrownBy(() -> {
            pathService.find(stationIds.get(교대역), stationIds.get(신사역));
        }).isInstanceOf(FindPathException.class)
                .hasMessage(ErrorType.NOT_EXIST_PATH.getMessage());
    }
}
