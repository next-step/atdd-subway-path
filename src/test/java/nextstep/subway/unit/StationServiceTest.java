package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.ui.exception.StationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(stationRepository);
    }

    @DisplayName("존재하지 않는 역")
    @Test
    void exceptionNotExistsStation() {
        // given
        Long lindId = 1L;
        when(stationRepository.findById(lindId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stationService.findById(lindId))
                .isInstanceOf(StationException.class)
                .hasMessage(String.format("존재하지 않는 역입니다. id = %d", lindId));
    }
}
