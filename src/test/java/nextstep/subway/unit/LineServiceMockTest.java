package nextstep.subway.unit;

import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.application.dto.SectionCreateRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        var 강남역 = new Station(1L, "강남역");
        var 역삼역 = new Station(2L, "역삼역");
        var 이호선 = new Line(1L, "이호선", "green");
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.retrieveById(any())).thenReturn(Optional.of(강남역));
        when(stationService.retrieveById(any())).thenReturn(Optional.of(역삼역));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionCreateRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        var line = lineService.retrieveById(1L).orElseThrow(
                () -> new IllegalArgumentException("해당하는 노선이 없습니다.")
        );
        assertThat(line.getSections()).hasSize(1);
    }
}
