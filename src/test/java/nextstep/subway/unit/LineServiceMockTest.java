package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.dto.LineDto;
import nextstep.subway.service.dto.LineSectionDto;
import nextstep.subway.service.dto.AddSectionCommand;
import nextstep.subway.service.dto.StationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static nextstep.subway.helper.fixture.LineFixture.신분당선_엔티티;
import static nextstep.subway.helper.fixture.StationFixture.*;
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
    @DisplayName("addSection을 호출하면 섹션이 추가된다.")
    void addSection() {
        Line 신분당선 = 신분당선_엔티티(강남역_엔티티, 역삼역_엔티티);

        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findStationById(역삼역_엔티티.getId())).thenReturn(역삼역_엔티티);
        when(stationService.findStationById(선릉역_엔티티.getId())).thenReturn(선릉역_엔티티);
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));

        // when
        // lineService.addSection 호출
        AddSectionCommand command = new AddSectionCommand(
                신분당선.getId(), 역삼역_엔티티.getId(), 선릉역_엔티티.getId(), 10
        );
        LineSectionDto createdSection = lineService.addSection(command);

        // then
        // lineService.findLineById 메서드를 통해 검증
        LineDto foundLine = lineService.getLineByIdOrFail(신분당선.getId());

        List<String> actualLineStationNames = foundLine.getStations()
                .stream().map(StationDto::getName)
                .collect(Collectors.toList());

        Set<String> addedStationNames = Set.of(
                createdSection.getUpStation().getName(),
                createdSection.getDownStation().getName()
        );

        assertTrue(actualLineStationNames.containsAll(addedStationNames));
    }
}
