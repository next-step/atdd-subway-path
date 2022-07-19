package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void 구간_추가() {
        // given
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");

        when(stationService.findById(upStationId)).thenReturn(new Station("강남"));
        when(stationService.findById(downStationId)).thenReturn(new Station("양재"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, 6));

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void 구간_삭제() {
        // given
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");
        line.addSection(upStationId, downStationId, 6);

        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        lineService.deleteSection(lineId, downStationId);

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void 구간_삭제_종점이_아니면_예외() {
        // given
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");
        line.addSection(upStationId, downStationId, 6);

        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when + then
        assertThatThrownBy(() -> lineService.deleteSection(lineId, upStationId))
                .isInstanceOf(CannotDeleteSectionException.class);
    }
}
