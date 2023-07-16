package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.PathException;
import nextstep.subway.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathFinderMockTest {

    @Mock
    private SectionRepository sectionRepository;

    private String 출발역명 = "출발역";
    private String 중간역명 = "중간역";
    private String 도착역명 = "도착역";
    private Station 출발역;
    private Station 중간역;
    private Station 도착역;
    private Line 노선;
    private Section 출발역_중간역;
    private Section 중간역_도착역;
    private Section 출발역_도착역;

    private PathFinder pathFinder;

    @BeforeEach
    public void setUp() {
        출발역 = new Station(1L, 출발역명);
        중간역 = new Station(2L, 중간역명);
        도착역 = new Station(3L, 도착역명);
        노선 = new Line();
        출발역_중간역 = new Section(노선, 출발역, 중간역, 5);
        중간역_도착역 = new Section(노선, 중간역, 도착역, 10);
        출발역_도착역 = new Section(노선, 출발역, 도착역, 16);

        pathFinder = new PathFinder(sectionRepository);
    }

    @Test
    void find() {
        //when
        when(sectionRepository.findAll()).thenReturn(List.of(출발역_중간역, 중간역_도착역, 출발역_도착역));

        //then
        PathResponse pathResponse = pathFinder.find(1L, 3L);
        List<String> stationNames = pathResponse.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly(출발역명, 중간역명, 도착역명);
        assertThat(pathResponse.getDistance()).isEqualTo(15);
    }

    @Test
    void 출발역과_도착역이_같은_경우() {
        //then
        assertThatThrownBy(() -> pathFinder.find(1L, 1L))
                .isInstanceOf(PathException.class)
                .hasMessage("출발역과 도착역이 같습니다.");
    }

    @Test
    void 존재하지_않는_출발역인_경우() {
        //then
        assertThatThrownBy(() -> pathFinder.find(100_000_000L, 1L))
                .isInstanceOf(StationNotFoundException.class);
    }

    @Test
    void 연결되어_있지_않는_출발역과_도착역인_경우() {
        //when
        Station 동떨어진역 = new Station(4L, "동떨어진역");
        Station 더동떨어진역 = new Station(5L, "더동떨어진역");
        Section 동떨어진구간 = new Section(new Line(), 동떨어진역, 더동떨어진역, 10);
        when(sectionRepository.findAll()).thenReturn(List.of(출발역_중간역, 중간역_도착역, 출발역_도착역, 동떨어진구간));

        //then
        assertThatThrownBy(() -> pathFinder.find(1L, 4L))
                .isInstanceOf(PathException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }
}
