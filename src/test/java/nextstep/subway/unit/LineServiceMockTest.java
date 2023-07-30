package nextstep.subway.unit;

import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.CreateSectionRequest;
import nextstep.subway.station.service.StationFindable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.fixture.LineFixture.신분당선;
import static nextstep.fixture.LineFixture.신분당선_ID;
import static nextstep.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationFindable stationFindable;

    private LineService lineService;

    @BeforeEach
    void init() {
        lineService = new LineService(lineRepository, stationFindable);
    }

    @Test
    void addSection() {
        // given
        CreateSectionRequest request = new CreateSectionRequest(
                신논현역_ID,
                논현역_ID,
                5L
        );
        given(lineRepository.findById(신분당선_ID)).willReturn(Optional.of(신분당선()));
        given(stationFindable.findStationById(신논현역_ID)).willReturn(신논현역());
        given(stationFindable.findStationById(논현역_ID)).willReturn(논현역());

        // when
        lineService.addSection(신분당선_ID, request);

        // then
        assertThat(lineService.findLineById(신분당선_ID).getSections().size()).isEqualTo(2);
    }
}
