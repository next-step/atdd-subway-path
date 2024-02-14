package nextstep.subway.unit;

import nextstep.subway.line.*;
import nextstep.subway.section.LineSections;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
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
        Section section = new Section(구의역, 강변역, 4, line.getId());

        // when
        // lineService.addSection 호출
        lineService.addSection(section);


        // then
        Mockito.when(lineService.findLine(any()))
                .thenReturn(new LineResponse(1L, "2호선", Color.GREEN, new StationResponse(1L, "구의역"), new StationResponse(3L, "강변역")));
        // lineService.findLineById 메서드를 통해 검증
        LineResponse 이호선 = lineService.findLine(line.getId());
        assertThat(이호선.getStations().stream().map(StationResponse::getId).collect(Collectors.toList()))
                .containsExactly(1L, 3L);
    }
}
