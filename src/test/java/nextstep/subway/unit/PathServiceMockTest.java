package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.sections.NotFoundStationException;
import org.assertj.core.api.ThrowableAssert;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @InjectMocks
    private PathService pathService;

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    Long 없는역Id = 1234L;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    /**
     * 교대역(1L    --- *2호선(10)* ---   강남역(2L)
     * |                                    |
     * *3호선(5)*                        *신분당선(3)*
     * |                                    |
     * 남부터미널역(4L)  --- *3호선(5)* ---   양재(3L)
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(5L, "2호선", "green");
        삼호선 = new Line(6L, "3호선", "yellow");
        신분당선 = new Line(7L, "신분당선", "red");

        이호선.addSection(new Section(8L, 이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(9L, 신분당선, 강남역, 양재역, 3));
        삼호선.addSection(new Section(10L, 삼호선, 교대역, 남부터미널역, 5));
        삼호선.addSection(new Section(11L, 삼호선, 남부터미널역, 양재역, 5));
    }

    @DisplayName("출발역이 존재하지 않는경우 예외발생")
    @Test
    public void not_exists_start_station() {
        // given
        given(stationService.findById(없는역Id)).willThrow(new NotFoundStationException());

        //when
        ThrowableAssert.ThrowingCallable actual = () -> pathService.findShortestPath(없는역Id, 교대역.getId());

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundStationException.class)
                .hasMessage("해당 역을 찾을 수 없습니다");
    }

    @DisplayName("도착역이 존재하지 않는경우 예외발생")
    @Test
    public void not_exists_arrival_station() {
        // given
        given(stationService.findById(교대역.getId())).willReturn(교대역);
        given(stationService.findById(없는역Id)).willThrow(new NotFoundStationException());

        //when
        ThrowableAssert.ThrowingCallable actual = () -> pathService.findShortestPath(교대역.getId(), 없는역Id);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundStationException.class)
                .hasMessage("해당 역을 찾을 수 없습니다");
    }

    @Test
    public void find_shortest_path() {
        // given
        given(stationService.findById(2L)).willReturn(강남역);
        given(stationService.findById(4L)).willReturn(남부터미널역);
        given(lineService.getAllLines()).willReturn(List.of(이호선, 삼호선, 신분당선));

        // when
        PathResponse response = pathService.findShortestPath(2L, 4L);
        List<String> collect = response.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(collect).containsExactly("강남역", "양재역", "남부터미널역"),
                () -> assertThat(response.getDistance()).isEqualTo(8)
        );
    }
}
