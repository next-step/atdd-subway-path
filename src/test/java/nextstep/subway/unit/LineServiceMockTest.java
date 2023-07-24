package nextstep.subway.unit;

import nextstep.subway.line.repository.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.CreateSectionRequest;
import nextstep.subway.section.repository.Section;
import nextstep.subway.station.repository.Station;
import nextstep.subway.station.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void init() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        CreateSectionRequest request = new CreateSectionRequest(
                1L,
                2L,
                10L
        );
        Long lineId = 1L;
        Line line = Line
                .builder()
                .name("신분당선")
                .color("bg-red-600")
                .initSection(Section.builder()
                        .upStation(new Station("강남역"))
                        .downStation(new Station("신논현역"))
                        .distance(10L)
                        .build())
                .build();
        Station upStation = new Station("신논현역");
        Station downStation = new Station("논현역");
        given(lineRepository.findById(lineId)).willReturn(Optional.of(line));
        given(stationService.findStationById(request.getUpStationId())).willReturn(upStation);
        given(stationService.findStationById(request.getDownStationId())).willReturn(downStation);

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findLineById(lineId).getSections().size()).isEqualTo(2);
    }
}
