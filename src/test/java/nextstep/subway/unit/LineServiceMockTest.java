package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        final long lineId = 1L;
        final long upStationId = 1L;
        final long downStationId = 2L;

        Line line = new Line("2호선", "bg-green-600");
        doReturn(Optional.of(line)).when(lineRepository).findById(lineId);

        Station gangnam = new Station("강남역");
        Station yeoksam = new Station("역삼역");
        doReturn(gangnam).when(stationService).findById(upStationId);
        doReturn(yeoksam).when(stationService).findById(downStationId);

        // when
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);
        lineService.addSection(lineId, sectionRequest);

        // then
        assertThat(line.getStations()).containsOnly(gangnam, yeoksam);
    }

    @Test
    @DisplayName("구간을 삭제하면 해당 구간의 하행역을 조회할 수 없다.")
    void deleteSection() {
        // given
        final long lineId = 1L;
        final long seolleungId = 3L;

        Station gangnam = new Station("강남역");
        Station yeoksam = new Station("역삼역");
        Station seolleung = new Station("선릉역");
        Line line = new Line("2호선", "bg-green-600");
        line.addSection(gangnam, yeoksam, 10);
        line.addSection(yeoksam, seolleung, 10);

        doReturn(Optional.of(line)).when(lineRepository).findById(lineId);
        doReturn(seolleung).when(stationService).findById(seolleungId);

        // when
        lineService.deleteSection(lineId, seolleungId);

        // then
        assertThat(line.getStations()).doesNotContain(seolleung);
    }

    @Test
    void deleteSection_invalid() {
        // given
        final long lineId = 1L;
        final long gangnamId = 1L;

        Station gangnam = new Station("강남역");
        Station yeoksam = new Station("역삼역");
        Station seolleung = new Station("선릉역");
        Line line = new Line("2호선", "bg-green-600");
        line.addSection(gangnam, yeoksam, 10);
        line.addSection(yeoksam, seolleung, 10);

        doReturn(Optional.of(line)).when(lineRepository).findById(lineId);
        doReturn(gangnam).when(stationService).findById(gangnamId);

        // when
        assertThatThrownBy(() -> lineService.deleteSection(lineId, gangnamId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
