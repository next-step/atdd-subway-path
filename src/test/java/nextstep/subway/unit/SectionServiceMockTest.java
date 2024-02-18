package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.SectionRequest;
import nextstep.subway.section.SectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {
    @InjectMocks
    SectionService sectionService;

    @InjectMocks
    LineService lineService;

    @Mock
    SectionRepository sectionRepository;

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    @Test
    @DisplayName("구간에 역을 추가한다.")
    void addSection() {
        // given
        Line 이호선 = new Line(1L, "2호선", "green");
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");

        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));
        when(stationRepository.findById(강남역.getId())).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(선릉역.getId())).thenReturn(Optional.of(선릉역));

        // when
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 선릉역.getId(), 7);
        sectionService.addSection(이호선, sectionRequest);

        // then
        assertThat(lineService.findLineById(이호선.getId()).getStations()).hasSize(2);
    }

    @Test
    @DisplayName("구간 중간에 역을 추가한다.")
    void addMiddleSection() {
        //given
        Line 이호선 = new Line(1L, "2호선", "green");
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");
        Station 신규역 = new Station(3L, "역삼역");

        Section 강남_선릉_구간 = new Section(1L, 강남역, 선릉역, 10, null); //기존

        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 신규역.getId(), 3);

        when(stationRepository.findById(강남역.getId())).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(신규역.getId())).thenReturn(Optional.of(신규역));
        when(sectionRepository.findByUpStation(강남역)).thenReturn(강남_선릉_구간);

        when(sectionRepository.save(any(Section.class))).then(AdditionalAnswers.returnsFirstArg());
        when(sectionRepository.findByDownStation(강남역)).thenReturn(null);

        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        //when
        sectionService.addSection(이호선, sectionRequest);

        //then
        assertThat(lineService.findLineById(이호선.getId()).getStations()).hasSize(3);
    }
}
