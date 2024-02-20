package nextstep.subway.unit;

import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
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
    private StationRepository stationRepository;
    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("addSection을 호출하면 섹션이 추가된다.")
    void addSection() {
        final Long lineId = 1L;
        final Long upStationId = 2L;
        final Long downStationId = 3L;

        // given
        when(stationRepository.findById(upStationId)).thenReturn(Optional.ofNullable(역삼역_엔티티));
        when(stationRepository.findById(downStationId)).thenReturn(Optional.ofNullable(선릉역_엔티티));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(신분당선_엔티티(강남역_엔티티, 역삼역_엔티티)));

        // when
        AddSectionCommand command = new AddSectionCommand(lineId, upStationId, downStationId, 10);
        LineSectionDto createdSection = lineService.addSection(command);

        // then
        LineDto foundLine = lineService.getLineByIdOrFail(lineId);

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
