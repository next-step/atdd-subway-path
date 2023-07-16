package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathFinderMockTest {

    @Mock
    private SectionRepository sectionRepository;

    @Test
    void find() {
        //given
        Station 출발역 = new Station(1L, "출발역");
        Station 중간역 = new Station(2L, "중간역");
        Station 도착역 = new Station(3L, "도착역");
        Line 노선 = new Line();
        Section 출발역_중간역 = new Section(노선, 출발역, 중간역, 5);
        Section 중간역_도착역 = new Section(노선, 중간역, 도착역, 10);
        Section 출발역_도착역 = new Section(노선, 출발역, 도착역, 16);

        //when
        when(sectionRepository.findAll()).thenReturn(List.of(출발역_중간역, 중간역_도착역, 출발역_도착역));
        PathFinder pathFinder = new PathFinder(sectionRepository);

        //then
        PathResponse pathResponse = pathFinder.find(1L, 3L);
        List<String> stationNames = pathResponse.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("출발역", "중간역", "도착역");
        assertThat(pathResponse.getDistance()).isEqualTo(15f);
    }
}
