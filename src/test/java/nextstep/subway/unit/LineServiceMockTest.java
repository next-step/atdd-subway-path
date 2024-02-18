package nextstep.subway.unit;

import nextstep.subway.line.*;
import nextstep.subway.line.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        Station 건대입구역 = new Station("건대입구역");
        Station 구의역 = new Station("구의역");
        Station 강변역 = new Station("강변역");

        Line line = new Line("2호선", Color.GREEN, 건대입구역, 구의역, 6);
        Section section = new Section(구의역, 강변역, 4, line);

        // when
        // lineService.addSection 호출
        lineService.addSection(line, section);


        // then
        List<StationResponse> stationResponses = List.of(
                new StationResponse(건대입구역.getId(), 건대입구역.getName()),
                new StationResponse(구의역.getId(), 구의역.getName()),
                new StationResponse(강변역.getId(), 강변역.getName())
        );
        Mockito.when(lineService.findLine(any()))
                .thenReturn(new LineResponse(1L, "2호선", Color.GREEN, stationResponses));

        // lineService.findLineById 메서드를 통해 검증
        LineResponse 이호선 = lineService.findLine(line.getId());
        assertThat(이호선.getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly(건대입구역.getName(), 구의역.getName(), 강변역.getName());
    }
}
