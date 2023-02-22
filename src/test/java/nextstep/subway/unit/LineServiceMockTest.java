package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
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
import static org.mockito.BDDMockito.given;

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
        Long 장승배기역_id = 1L;
        Long 상도역_id = 2L;
        Long 칠호선_id = 1L;

        given(stationService.findById(장승배기역_id)).willReturn(new Station(장승배기역_id, "장승배기역"));
        given(stationService.findById(상도역_id)).willReturn(new Station(상도역_id, "상도역"));
        given(lineRepository.findById(칠호선_id)).willReturn(Optional.of(new Line("7호선", "green darken-2")));


        // when
        lineService.addSection(칠호선_id, createSectionRequest(상도역_id, 칠호선_id));

        // then
        var lineResponse = lineService.findLineById(칠호선_id).getSections();
        assertThat(lineResponse.getStations()).extracting("name").contains("상도역", "장승배기역");
    }

    private SectionRequest createSectionRequest(Long upStationId, Long downStationId) {
        return new SectionRequest(upStationId, downStationId, 10);
    }
}
