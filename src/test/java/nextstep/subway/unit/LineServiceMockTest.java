package nextstep.subway.unit;

import static org.mockito.BDDMockito.*;
import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.application.LineService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionAddRequest;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        given(lineRepository.findById(any())).willReturn(Optional.of(new Line(1L, 신분당선_이름, 신분당선_색상)));
        given(stationRepository.findById(1L)).willReturn(Optional.of(new Station(1L, 신사역)));
        given(stationRepository.findById(2L)).willReturn(Optional.of(new Station(2L, 논현역)));
        SectionAddRequest sectionAddRequest = new SectionAddRequest(1L, 2L, 10);

        // when
        Line actual = lineService.addSection(1L, sectionAddRequest);

        // then
        Assertions.assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(lineService.findById(actual.getId()));
    }
}
