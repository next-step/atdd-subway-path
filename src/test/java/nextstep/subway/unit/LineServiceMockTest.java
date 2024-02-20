package nextstep.subway.unit;

import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.dto.request.CreateSectionRequest;
import nextstep.subway.domain.line.dto.response.LineResponse;
import nextstep.subway.domain.line.repository.LineRepository;
import nextstep.subway.domain.line.service.SectionService;
import nextstep.subway.domain.station.domain.Station;
import nextstep.subway.domain.station.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private SectionService sectionService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        when(lineRepository.getLineById(any())).thenReturn(Line.builder()
                .name("2호선")
                .color("red")
                .build());

        when(stationRepository.getById(any())).thenReturn(강남역);
        when(stationRepository.getById(any())).thenReturn(역삼역);

        // when
        // lineService.addSection 호출
        LineResponse lineResponse = sectionService.createSection(1L,
                CreateSectionRequest
                        .builder()
                        .upStationId(강남역.getId())
                        .downStationId(역삼역.getId())
                        .distance(10)
                        .build()
        );

        // then
        assertThat(lineResponse.getSections()).hasSize(1);
    }
}
