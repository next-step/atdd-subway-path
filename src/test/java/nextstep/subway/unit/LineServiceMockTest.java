package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sectioncondition.SectionCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.utils.LineTestSources.*;
import static nextstep.subway.utils.StationTestSources.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService target;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @Mock
    private SectionCondition sectionCondition;

    @Test
    void deleteSectionFail_LineNotExists() {
        // given
        doReturn(Optional.empty())
                .when(lineRepository)
                .findById(lineId);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.deleteSection(lineId, downStationId));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSectionFail_NotLastDownStation() {
        // given
        final Line line = line();
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(lineId);

        final Station upStation = upStation();
        doReturn(upStation)
                .when(stationService)
                .findById(upStation.getId());

        line.addSection(upStation, new Station(200L, "A"), 10);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.deleteSection(lineId, upStation.getId()));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void deleteSectionSuccess() {
        // given
        final Line line = line();
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(lineId);

        final Station downStation = downStation();
        doReturn(downStation)
                .when(stationService)
                .findById(downStation.getId());
        line.addSection(upStation(), downStation, 10);

        // when
        target.deleteSection(lineId, downStation.getId());

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void saveLineSuccess_NotSection() {
        // given

        doReturn(line())
                .when(lineRepository)
                .save(any(Line.class));

        // when
        final LineResponse result = target.saveLine(lineRequest());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isEmpty();
    }

    @Test
    void saveLineSuccess_WithSection() {
        // given

        doReturn(line())
                .when(lineRepository)
                .save(any(Line.class));

        doReturn(upStation())
                .when(stationService)
                .findById(upStationId);

        doReturn(downStation())
                .when(stationService)
                .findById(downStationId);

        doReturn(true)
                .when(sectionCondition)
                .isSatisfiedBy(any(Line.class), any(AddSectionRequest.class));

        // when
        final LineResponse result = target.saveLine(lineRequest(upStationId, downStationId));

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void addSectionSuccess() {
        // given
        final Line line = line();
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(lineId);

        doReturn(upStation())
                .when(stationService)
                .findById(upStationId);

        doReturn(downStation())
                .when(stationService)
                .findById(downStationId);

        doReturn(true)
                .when(sectionCondition)
                .isSatisfiedBy(any(Line.class), any(AddSectionRequest.class));

        // when
        target.addSection(lineId, sectionRequest(upStationId, downStationId));

        // then
        verify(sectionCondition).add(any(Line.class), any(AddSectionRequest.class));
    }

    @Test
    void addSectionFail() {
        // given
        final Line line = line();
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(lineId);

        doReturn(upStation())
                .when(stationService)
                .findById(upStationId);

        doReturn(downStation())
                .when(stationService)
                .findById(downStationId);

        doReturn(false)
                .when(sectionCondition)
                .isSatisfiedBy(any(Line.class), any(AddSectionRequest.class));

        // when
        target.addSection(lineId, sectionRequest(upStationId, downStationId));

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void addSectionFail_LineNotFound() {
        // given
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
        final LineRequest lineRequest = lineRequest();

        final Line line = line();
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
        doReturn(Optional.empty())
                .when(lineRepository)
                .findById(lineId);

        // when
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> target.updateLine(lineId, lineRequest()));

        // then
        assertThat(result).isNotNull();
    }

}
