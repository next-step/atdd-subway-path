package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.dto.LineDto;
import nextstep.subway.service.dto.LineSectionDto;
import nextstep.subway.service.dto.SaveLineSectionCommand;
import nextstep.subway.service.dto.StationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
        Station 강남역 = new Station(1L, "강남역"); //상행역
        Station 역삼역 = new Station(2L, "역삼역"); //하행역
        Station 선릉역 = new Station(3L, "선릉역"); //하행역

        Line 신분당선 = Line.createWithId(1L, "신분당선", "bg-red-600", 강남역, 역삼역, 10);

        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findStationById(선릉역.getId())).thenReturn(선릉역);
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));

        // when
        // lineService.addSection 호출
        SaveLineSectionCommand command = new SaveLineSectionCommand(신분당선.getId(), 역삼역.getId(), 선릉역.getId(), 10);
        LineSectionDto createdSection = lineService.saveLineSection(command);

        // then
        // lineService.findLineById 메서드를 통해 검증

        LineDto foundLine = lineService.getLineByIdOrFail(신분당선.getId());

        List<String> actualLineStationNames = foundLine.getStations()
                .stream().map(StationDto::getName)
                .collect(Collectors.toList());

        Set<String> expectedLineStationNames = Set.of(
                createdSection.getUpStation().getName(),
                createdSection.getDownStation().getName()
        );

        assertTrue(actualLineStationNames.containsAll(expectedLineStationNames));
    }
}
