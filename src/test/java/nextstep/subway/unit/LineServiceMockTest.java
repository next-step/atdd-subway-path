package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService target;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void saveLineSuccess_NotSection() {
        // given

        doReturn(new Line("name", "color"))
                .when(lineRepository)
                .save(any(Line.class));

        // when
        final LineResponse result = target.saveLine(new LineRequest("name", "color"));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isEmpty();
    }

    @Test
    void saveLineSuccess_WithSection() {
        // given

        doReturn(new Line("name", "color"))
                .when(lineRepository)
                .save(any(Line.class));

        final long upStationId = 100L;
        doReturn(mock(Station.class))
                .when(stationService)
                .findById(upStationId);

        final long downStationId = 200L;
        doReturn(mock(Station.class))
                .when(stationService)
                .findById(downStationId);

        // when
        final LineResponse result = target.saveLine(new LineRequest("name", "color", upStationId, downStationId, 1));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isNotEmpty();
    }

    @Test
    void addSectionSuccess() {
        // given
        final long lineId = 1L;
        final Line line = new Line("name", "color");
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(lineId);

        final long upStationId = 100L;
        doReturn(mock(Station.class))
                .when(stationService)
                        .findById(upStationId);

        final long downStationId = 200L;
        doReturn(mock(Station.class))
                .when(stationService)
                        .findById(downStationId);

        // when
        target.addSection(lineId, new SectionRequest(upStationId, downStationId, 1));

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void addSectionFail_LineNotFound() {
        // given
        final long lineId = 1L;
        doReturn(Optional.empty())
                .when(lineRepository)
                .findById(lineId);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.addSection(lineId, new SectionRequest()));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateLineSuccess() {
        // given
        final long lineId = 1L;
        final LineRequest lineRequest = new LineRequest("newName", "newColor");

        final Line line = new Line("name", "color");
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(lineId);

        // when
        target.updateLine(lineId, lineRequest);

        // then
        assertThat(line.getName()).isEqualTo(lineRequest.getName());
        assertThat(line.getColor()).isEqualTo(lineRequest.getColor());
    }

    @Test
    void updateLineFail_LineNotFound() {
        // given
        final long lineId = 1L;
        doReturn(Optional.empty())
                .when(lineRepository)
                .findById(lineId);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.updateLine(lineId, new LineRequest(null, null)));

        // then
        assertThat(result).isNotNull();
    }

}
