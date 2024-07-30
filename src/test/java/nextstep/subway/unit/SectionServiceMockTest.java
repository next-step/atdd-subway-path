package nextstep.subway.unit;

import nextstep.subway.domain.SectionBuilder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayLineBuilder;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.repository.SubwayLineRepository;
import nextstep.subway.service.SectionService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.SubwayLineService;
import nextstep.subway.utils.Persistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nextstep.subway.utils.Constants.*;
import static nextstep.subway.utils.Constants.PANGYO_STATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {
    @Mock
    private SubwayLineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Mock
    private SubwayLineService subwayLineService;

    private Persistence persistence = new Persistence();

    @Test
    @DisplayName("지하철 노선 구간을 등록한다")
    void addSection() {
        // given
        var sectionService = new SectionService(subwayLineService, stationService);
        var lineUpStation = persistence.persist(new Station(GANGNAM_STATION));
        var lineDownStation = persistence.persist(new Station(PANGYO_STATION));
        var sectionDownStationToAdd = persistence.persist(new Station(YONGSAN_STATION));
        var section = persistence.persist(new SectionBuilder()
                .distance(10L)
                .upStation(lineUpStation)
                .downStation(lineDownStation)
                .build());
        var subwayLine = persistence.persist(
                new SubwayLineBuilder()
                        .name(LINE_SINBUNDANG)
                        .color(COLOR_RED)
                        .section(section)
                        .build()
        );
        when(subwayLineService.findSubwayLineOrElseThrow(subwayLine.getId())).thenReturn(subwayLine);
        when(stationService.findStationOrElseThrow(lineDownStation.getId())).thenReturn(lineDownStation);
        when(stationService.findStationOrElseThrow(sectionDownStationToAdd.getId())).thenReturn(sectionDownStationToAdd);

        // when
        var newSection = new SectionRequest.SectionRequestBuilder()
                .distance(10L)
                .upStationId(lineDownStation.getId())
                .downStationId(sectionDownStationToAdd.getId())
                .build();

        assertDoesNotThrow(() -> sectionService.save(subwayLine.getId(), newSection));
    }
}
