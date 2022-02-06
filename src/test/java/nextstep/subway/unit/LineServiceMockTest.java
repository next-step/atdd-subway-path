package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private SectionRepository sectionRepository;

    @Test
    void addSection() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 판교역 = new Station(2L, "판교역");
        Line 신분당선 = new Line(1L, "신분당선", "red");
        신분당선.addSection(강남역, 판교역, 8);
        Station 정자역 = new Station(3L, "정자역");
        Section 판교역_정자역_구간 = new Section(2L, 신분당선, 판교역, 정자역, 4);
        when(lineRepository.findById(신분당선.getId()))
                .thenReturn(Optional.of(신분당선));
        when(stationRepository.findById(판교역.getId()))
                .thenReturn(Optional.of(판교역));
        when(stationRepository.findById(정자역.getId()))
                .thenReturn(Optional.of(정자역));
        when(sectionRepository.save(any())).thenReturn(판교역_정자역_구간);

        // when
        SectionResponse sectionResponse = lineService.addSection(
                new SectionRequest(판교역.getId(), 정자역.getId(), 판교역_정자역_구간.getDistance()),
                신분당선.getId());

        // then
        assertThat(sectionResponse.getId()).isNotNull();
        assertThat(sectionResponse.getLineId()).isNotNull();
        assertThat(sectionResponse.getUpStationId()).isNotNull();
        assertThat(sectionResponse.getDownStationId()).isNotNull();
        assertThat(sectionResponse.getDistance()).isGreaterThan(0);
    }
}
