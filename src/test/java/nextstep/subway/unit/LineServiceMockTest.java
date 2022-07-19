package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.fixture.ConstStation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationRepository.findById(1L)).thenReturn(Optional.of(ConstStation.강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(ConstStation.신논현역));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(Line.of("신분당선", "bg-red-600")));
        LineService lineService = new LineService(lineRepository, stationRepository);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, SectionRequest.of(1L, 2L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        Line findLine = lineService.findLineById(1L);

        assertAll(
                () -> assertThat(findLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(findLine.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(findLine.getSections()).hasSize(1),
                () -> assertThat(findLine.allStations()).extracting("name").containsExactly("강남역", "신논현역")
        );
    }
}
