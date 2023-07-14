package nextstep.subway.unit;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.service.LineReadService;
import nextstep.subway.section.model.SectionCreateRequest;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.section.service.SectionManageService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private StationService stationService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private LineReadService lineReadService;

    @InjectMocks
    private SectionManageService sectionManageService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        long givenStationAId = 1L;
        Station stationA = new Station(givenStationAId, "stationA");
        long givenStationBId = 2L;
        Station stationB = new Station(givenStationBId, "stationB");

        long givenLineId = 10L;
        Line lineAB = new Line(givenLineId, "lineAB", "yellow", stationA, stationB, new Sections(new ArrayList<>()));

        Mockito.when(stationService.get(givenStationAId)).thenReturn(stationA);
        Mockito.when(stationService.get(givenStationBId)).thenReturn(stationB);
        Mockito.when(lineReadService.getLine(givenLineId)).thenReturn(lineAB);

        Mockito.when(sectionRepository.save(Mockito.any())).then(answer -> answer.getArgument(0));

        // when
        // lineService.addSection 호출
        sectionManageService.create(lineAB.getId(), new SectionCreateRequest(givenStationBId, givenStationAId, 10));


        // then
        // lineService.findLineById 메서드를 통해 검증
        Line expectedLineAB = lineReadService.getLine(givenLineId);
        Assertions.assertThat(expectedLineAB.getSections().isEmpty()).isFalse();

    }
}
